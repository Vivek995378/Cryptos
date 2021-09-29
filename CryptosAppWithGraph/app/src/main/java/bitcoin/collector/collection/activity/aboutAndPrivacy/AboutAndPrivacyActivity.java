package bitcoin.collector.collection.activity.aboutAndPrivacy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import bitcoin.collector.collection.R;

import static bitcoin.collector.collection.util.AppUtil.setStatusTheme;

public class AboutAndPrivacyActivity extends AppCompatActivity {

    private TextView tvTitle, tvContent;
    private String aboutUs = "We are a digital platform that helps you get all the information about the cryptocurrency and try to make the accessibility as easy as we can.\n" +
            "\n" +
            "We believe cryptocurrency has the ability to shape the future and can bring new and extraordinary opportunities for everyone around the world.\n" +
            "\n" +
            "\n" +
            "\n" +
            "With this perspective in mind we would like to welcome you on our app.\n" +
            "\n" +
            "Our App helps you view the prices of various cryptocurrencies all around the world and the change in their prices.\n" +
            "\n" +
            "\n" +
            "\n" +
            "Further with our app you can collect coins and use it to withdraw your favorite cryptos directly into your wallet. Collect more coins and withdraw unlimited cryptocurrencies in your wallet at no cost.\n" +
            "\n" +
            "\n" +
            "\n" +
            "With our Converter tool you can convert and view any cryptocurrency and how it stands in comparison to other coins.\n" +
            "\n" +
            "\n" +
            "\n" +
            "Also we give you personalization with your profile as you can change your profile and make it more personalized.\n" +
            "\n" +
            "\n" +
            "\n" +
            " We would like to Thank You for your constant support and if you have any feedback email us on cryptominingapps01@gmail.com";
    private String privacyPolicy = "We use the information we collect or receive:\n" +
            "\n" +
            "To facilitate account creation and logon process. If you choose to link your account with us to a third-party account (such as your Google or Facebook account), we use the information you allowed us to collect from those third parties to facilitate account creation and logon process for the performance of the contract. See the section below headed “HOW DO WE HANDLE YOUR SOCIAL LOGINS” for further information.\n" +
            "\n" +
            "Request feedback. We may use your information to request feedback and to contact you about your use of our Services.\n" +
            "\n" +
            "To enable user-to-user communications. We may use your information in order to enable user-to-user communications with each user’s consent.\n" +
            "\n" +
            "To manage user accounts. We may use your information for the purposes of managing our account and keeping it in working order.\n" +
            "\n" +
            "To send administrative information to you. We may use your personal information to send you product, service and new feature information and/or information about changes to our terms, conditions, and policies.\n" +
            "\n" +
            "To protect our Services. We may use your information as part of our efforts to keep our Services safe and secure (for example, for fraud monitoring and prevention).\n" +
            "\n" +
            "To enforce our terms, conditions and policies for business purposes, to comply with legal and regulatory requirements or in connection with our contract.\n" +
            "\n" +
            "To respond to legal requests and prevent harm. If we receive a subpoena or other legal request, we may need to inspect the data we hold to determine how to respond.\n" +
            "\n" +
            "Fulfill and manage your orders. We may use your information to fulfill and manage your orders, payments, returns, and exchanges made through the Services\n" +
            "\n" +
            "Administer prize draws and competitions. We may use your information to administer prize draws and competitions when you elect to participate in our competitions.\n" +
            "\n" +
            "To deliver and facilitate delivery of services to the user. We may use your information to provide you with the requested service.\n" +
            "\n" +
            "To respond to user inquiries/offer support to users. We may use your information to respond to your inquiries and solve any potential issues you might have with the use of our Services.\n" +
            "\n" +
            "To send you marketing and promotional communications. We and/or our third-party marketing partners may use the personal information you send to us for our marketing purposes, if this is in accordance with your marketing preferences. For example, when expressing an interest in obtaining information about us or our Services, subscribing to marketing or otherwise contacting us, we will collect personal information from you. You can opt-out of our marketing emails at any time (see the “WHAT ARE YOUR PRIVACY RIGHTS” below).\n" +
            "\n" +
            "Deliver targeted advertising to you. We may use your information to develop and display personalized content and advertising (and work with third parties who do so) tailored to your interests and/or location and to measure its effectiveness. This data may be stored by Coinzilla. You may find their privacy notice link(s) here: https://coinzilla.com/privacy-policy/.\n" +
            "\n" +
            "For other business purposes. We may use your information for other business purposes, such as data analysis, identifying usage trends, determining the effectiveness of our promotional campaigns and to evaluate and improve our Services, products, marketing and your experience. We may use and store this information in aggregated and anonymized form so that it is not associated with individual end users and does not include personal information. We will not use identifiable personal information without your consent.\n" +
            "\n" +
            "We may process or share your data that we hold based on the following legal basis:\n" +
            "\n" +
            "Consent: We may process your data if you have given us specific consent to use your personal information for a specific purpose.\n" +
            "\n" +
            "Legitimate Interests: We may process your data when it is reasonably necessary to achieve our legitimate business interests.\n" +
            "\n" +
            "Performance of a Contract: Where we have entered into a contract with you, we may process your personal information to fulfill the terms of our contract.\n" +
            "\n" +
            "Legal Obligations: We may disclose your information where we are legally required to do so in order to comply with applicable law, governmental requests, a judicial proceeding, court order, or legal process, such as in response to a court order or a subpoena (including in response to public authorities to meet national security or law enforcement requirements).\n" +
            "\n" +
            "Vital Interests: We may disclose your information where we believe it is necessary to investigate, prevent, or take action regarding potential violations of our policies, suspected fraud, situations involving potential threats to the safety of any person and illegal activities, or as evidence in litigation in which we are involved.\n" +
            "\n" +
            "More specifically, we may need to process your data or share your personal information in the following situations:\n" +
            "\n" +
            "Business Transfers. We may share or transfer your information in connection with, or during negotiations of, any merger, sale of company assets, financing, or acquisition of all or a portion of our business to another company.\n" +
            "\n" +
            "Vendors, Consultants and Other Third-Party Service Providers. We may share your data with third-party vendors, service providers, contractors or agents who perform services for us or on our behalf and require access to such information to do that work. Examples include: payment processing, data analysis, email delivery, hosting services, customer service and marketing efforts. We may allow selected third parties to use tracking technology on the Services, which will enable them to collect data on our behalf about how you interact with our Services over time. This information may be used to, among other things, analyze and track data, determine the popularity of certain content, pages or features, and better understand online activity. Unless described in this notice, we do not share, sell, rent or trade any of your information with third parties for their promotional purposes. We have contracts in place with our data processors, which are designed to help safeguard your personal information. This means that they cannot do anything with your personal information unless we have instructed them to do it. They will also not share your personal information with any organization apart from us. They also commit to protect the data they hold on our behalf and to retain it for the period we instruct.\n" +
            "\n" +
            "Third-Party Advertisers. We may use third-party advertising companies to serve ads when you visit or use the Services. These companies may use information about your visits to our Website(s) and other websites that are contained in web cookies and other tracking technologies in order to provide advertisements about goods and services of interest to you.\n" +
            "\n" +
            "Affiliates. We may share your information with our affiliates, in which case we will require those affiliates to honor this privacy notice. Affiliates include our parent company and any subsidiaries, joint venture partners or other companies that we control or that are under common control with us.\n" +
            "\n" +
            "Business Partners. We may share your information with our business partners to offer you certain products, services or promotions.\n" +
            "\n" +
            "\n" +
            "Our Services offers you the ability to register and login using your third-party social media account details (like your Facebook or Twitter logins). Where you choose to do this, we will receive certain profile information about you from your social media provider. The profile Information we receive may vary depending on the social media provider concerned, but will often include your name, email address, friends list, profile picture as well as other information you choose to make public on such social media platforms.\n" +
            "\n" +
            "We will use the information we receive only for the purposes that are described in this privacy notice or that are otherwise made clear to you on the relevant Services. Please note that we do not control, and are not responsible for, other uses of your personal information by your third-party social media provider. We recommend that you review their privacy notice to understand how they collect, use and share your personal information, and how you can set your privacy preferences on their sites and apps.\n" +
            "\n" +
            "\n" +
            "The Services may contain advertisements from third parties that are not affiliated with us and which may link to other websites, online services or mobile applications. We cannot guarantee the safety and privacy of data you provide to any third parties. Any data collected by third parties is not covered by this privacy notice. We are not responsible for the content or privacy and security practices and policies of any third parties, including other websites, services or applications that may be linked to or from the Services. You should review the policies of such third parties and contact them directly to respond to your questions.\n" +
            "\n" +
            "\n" +
            "We will only keep your personal information for as long as it is necessary for the purposes set out in this privacy notice, unless a longer retention period is required or permitted by law (such as tax, accounting or other legal requirements). No purpose in this notice will require us keeping your personal information for longer than the period of time in which users have an account with us.\n" +
            "\n" +
            "When we have no ongoing legitimate business need to process your personal information, we will either delete or anonymize such information, or, if this is not possible (for example, because your personal information has been stored in backup archives), then we will securely store your personal information and isolate it from any further processing until deletion is possible.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_and_privacy);
        setStatusTheme(this);
        tvTitle = findViewById(R.id.textView51);
        tvContent = findViewById(R.id.textView52);

        if (getIntent().getBooleanExtra("isAbout", false)) {
            tvTitle.setText("About Us");
            tvContent.setText(aboutUs);
        } else {
            tvTitle.setText("Privacy policy");
            tvContent.setText(privacyPolicy);
        }
    }

    public void backAboutAndPrivacy(View view) {
        onBackPressed();
    }
}