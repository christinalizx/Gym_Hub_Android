package edu.northeastern.gymhub.Activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import edu.northeastern.gymhub.R;

public class PersonalInformationDetailsPageActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information_details_page);
    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_right);
    }
}