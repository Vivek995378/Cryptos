package crypto.cryptocurrencies.cryptos.activity.help;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static crypto.cryptocurrencies.cryptos.database.FirestoreConstant.HELP_IMAGE_TABLE;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.hideProgressDialog;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.rotateImageIfRequired;
import static crypto.cryptocurrencies.cryptos.util.AppUtil.showProgressDialog;

public class HelpActivity extends AppCompatActivity {

    private EditText edtQuery;
    private ImageView imgSelected;
    private User mUser;
    private static final int PERMISSION_GALLERY_CODE = 1001;
    private String imgUrl = "";
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        AppUtil.setStatusBarGradiant(this);
        mUser = AppPreference.getUserDetails(this);
        edtQuery = findViewById(R.id.editText7);
        imgSelected = findViewById(R.id.imageView13);

    }

    public void backHelp(View view) {
        onBackPressed();
    }

    public void selectHelpImage(View view) {
        Intent gallery =
                new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityIfNeeded(gallery, PERMISSION_GALLERY_CODE);
    }

    public void validateHelpFields(View view) {
        if (edtQuery.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter your query", Toast.LENGTH_SHORT).show();
        } else if (mBitmap != null) {
            showProgressDialog(this);
            uploadImage();
        } else {
            saveData();
        }
    }

    private void saveData() {
        Map<String, Object> map = new HashMap<>();
        map.put("query", edtQuery.getText().toString());
        map.put("userId", mUser.getUserId().trim());
        if (!imgUrl.isEmpty()) {
            map.put("selectedImage", imgUrl);
        }
        map.put("creationDate", System.currentTimeMillis() / 1000);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("help")
                .document()
                .set(map);
        hideProgressDialog();
        try {
            if (!isDestroyed()) {
                showSubmitDialog(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uploadImage() {
        StorageReference mImageStorage = FirebaseStorage.getInstance().getReference();
        final StorageReference ref = mImageStorage.child(HELP_IMAGE_TABLE)
                .child(UUID.randomUUID().toString() + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
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
                Toast.makeText(HelpActivity.this, "Image upload exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                Uri imgUri = data.getData();
                String filePath = RealPathUtil.getPath(HelpActivity.this, imgUri);
                if (filePath != null) {
                    try {
                        mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                        mBitmap = rotateImageIfRequired(mBitmap, filePath);
                        Glide.with(this).load(imgUri).into(imgSelected);
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

    public static void showSubmitDialog(final Activity activity) {
        final Dialog submitDialog = new Dialog(activity);
        submitDialog.getWindow().setContentView(R.layout.help_dialog);
        submitDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        submitDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        submitDialog.show();

        Button btCancel = submitDialog.findViewById(R.id.button3);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitDialog.dismiss();
            }
        });

        submitDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                activity.onBackPressed();
            }
        });
    }
}