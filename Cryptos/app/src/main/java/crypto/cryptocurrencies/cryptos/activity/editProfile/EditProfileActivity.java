package crypto.cryptocurrencies.cryptos.activity.editProfile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cryptos.cryptocurrency.R;
import crypto.cryptocurrencies.cryptos.models.user.User;
import crypto.cryptocurrencies.cryptos.util.AppPreference;
import crypto.cryptocurrencies.cryptos.util.AppUtil;
import crypto.cryptocurrencies.cryptos.util.RealPathUtil;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static crypto.cryptocurrencies.cryptos.database.FirestoreConstant.USER_IMAGE_TABLE;
import static crypto.cryptocurrencies.cryptos.database.FirestoreConstant.USER_TABLE;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.getDirc;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.hideProgressDialog;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.rotateImageIfRequired;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.showProgressDialog;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView imgUser;
    private TextView tvChooseImage, tvMobileOrEmailHint, tvMobileOrEmail;
    private EditText edtName;
    private User mUser;
    private static final int PERMISSION_GALLERY_CODE = 1001;
    private static final int PERMISSION_CAMERA_CODE = 1002;
    private String imgUrl = "", photofile = "";
    private Bitmap mBitmap;
    private File picFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        AppUtil.setStatusBarGradiant(this);
        mUser = AppPreference.getUserDetails(this);

        imgUser = findViewById(R.id.imageView12);
        tvChooseImage = findViewById(R.id.textView40);
        edtName = findViewById(R.id.editText5);
        tvMobileOrEmailHint = findViewById(R.id.textView41);
        tvMobileOrEmail = findViewById(R.id.textView42);

        if (mUser.getProfilePic() != null) {
            Glide.with(this)
                    .load(mUser.getProfilePic())
                    .circleCrop()
                    .into(imgUser);
            imgUrl = mUser.getProfilePic();
        }

        if (mUser.getName() != null) {
            edtName.setText(mUser.getName());
        }

        if (mUser.getEmail() != null) {
            tvMobileOrEmailHint.setText("Email");
            tvMobileOrEmail.setText(mUser.getEmail());
        } else if (mUser.getPhone() != null) {
            tvMobileOrEmailHint.setText("Mobile");
            tvMobileOrEmail.setText(mUser.getPhone());
        }

        tvChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageTypeChooser();
            }
        });

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvChooseImage.performClick();
            }
        });
    }

    public void validateFields(View view) {
        if (edtName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
        } else if (imgUrl.isEmpty() && mBitmap == null) {
            Toast.makeText(this, "Please select your profile image", Toast.LENGTH_SHORT).show();
        } else if (mBitmap != null) {
            showProgressDialog(this);
            uploadImage(mBitmap);
        } else {
            saveData();
        }
    }

    private void uploadImage(Bitmap bitmap) {
        StorageReference mImageStorage = FirebaseStorage.getInstance().getReference();
        final StorageReference ref = mImageStorage.child(USER_IMAGE_TABLE)
                .child(UUID.randomUUID().toString() + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] data = baos.toByteArray();
        final UploadTask uploadTask = ref.putBytes(data);
        ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downUri = task.getResult();
                            imgUrl = downUri.toString();
                            mBitmap = null;
                            saveData();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                Toast.makeText(EditProfileActivity.this, "Image upload exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveData() {
        hideProgressDialog();
        mUser.setName(edtName.getText().toString().trim());
        mUser.setProfilePic(imgUrl);
        Map<String, Object> map = new HashMap<>();
        map.put("name", edtName.getText().toString().trim());
        map.put("profilePic", imgUrl);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(USER_TABLE).document(mUser.getUserId())
                .update(map);
        AppPreference.setUserDetails(this, mUser);
        try {
            Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_GALLERY_CODE) {
            boolean allOk = false;

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allOk = true;
                } else {
                    allOk = false;
                }
            }

            if (allOk) {
                openImageTypeChooser();
            } else {
                openUtilityDialog(EditProfileActivity.this,
                        "External Storage permission is required. " +
                                "Please allow this permission in App Settings.", false);
            }
        }
    }

    private void openImageTypeChooser() {
        if (checkCameraPermission(EditProfileActivity.this)
                || checkWriteStoragePermission(EditProfileActivity.this)
                || checkReadStoragePermission(EditProfileActivity.this)) {
            CharSequence[] items = {"Gallery", "Camera"};

            AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
            builder.setTitle("Select one");

            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int pos) {
                    if (pos == 0) {
                        Intent gallery =
                                new Intent(Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(gallery, PERMISSION_GALLERY_CODE);
                    } else if (pos == 1) {
                        openCameraIntent();
                    }
                }
            });
            builder.show();
        } else {
            askForPermission();
        }
    }

    private void askForPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(EditProfileActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            openUtilityDialog(EditProfileActivity.this,
                    "External Storage permission is required. " +
                            "Please allow this permission in App Settings.", false);
        } else {
            ActivityCompat.requestPermissions(EditProfileActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA}, PERMISSION_GALLERY_CODE);
        }
    }

    private void openCameraIntent() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            File file_image = getDirc();
            if (!file_image.exists() && !file_image.mkdirs()) {
                Toast.makeText(this, "Can't create directory to save image", Toast.LENGTH_SHORT).show();
                return;
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String date = simpleDateFormat.format(new Date());
            photofile = "AdStore" + date + ".jpg";

            File f = new File(getDirc(), photofile);
            Uri photoURI = FileProvider.getUriForFile(EditProfileActivity.this,
                    getPackageName()+".fileprovider", f);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                intent.setClipData(ClipData.newRawUri("", photoURI));
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            startActivityIfNeeded(intent, PERMISSION_CAMERA_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openUtilityDialog(final Context ctx, final String messageID, boolean isHide) {
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(ctx);
        dialog.setMessage(messageID);
        if (isHide) {
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }
        dialog.setPositiveButton("GO TO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Uri uri = Uri.fromParts("package", ctx.getPackageName(), null);
                intent.setData(uri);
                ctx.startActivity(intent);
                dialog.dismiss();
            }
        }).show();
    }

    private boolean checkCameraPermission(Activity activity) {
        int result = ContextCompat.checkSelfPermission(activity, CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkReadStoragePermission(Activity activity) {
        int result = ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkWriteStoragePermission(Activity activity) {
        int result = ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PERMISSION_CAMERA_CODE) {
                String file_name = getDirc().getAbsolutePath() + "/" + photofile;
                picFile = new File(file_name);
                Uri imgUri = Uri.fromFile(new File(String.valueOf(picFile)));
                try {
                    mBitmap = rotateImageIfRequired(MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri),
                            file_name);
                    Glide.with(this).load(imgUri).circleCrop().into(imgUser);
                } catch (Exception e) {
                    if (mBitmap != null) {
                        mBitmap = null;
                    }
                    Toast.makeText(this, "There is some problem with this image, Please try again", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == PERMISSION_GALLERY_CODE) {
                try {
                    Uri imgUri = data.getData();
                    String filePath = RealPathUtil.getPath(EditProfileActivity.this, imgUri);
                    if (filePath != null) {
                        try {
                            mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                            mBitmap = rotateImageIfRequired(mBitmap, filePath);
                            Glide.with(this).load(imgUri).circleCrop().into(imgUser);
                        } catch (Exception e) {
                            Toast.makeText(this, "There is some problem with this image", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    hideProgressDialog();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void backEditProfile(View view) {
        onBackPressed();
    }
}