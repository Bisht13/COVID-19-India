package com.adityabisht.covid_19india;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class TesterActivity extends AppCompatActivity {
    private Button backbutton;
    private CheckBox cough;
    private CheckBox fever;
    private CheckBox difficulty_in_breathing;
    private CheckBox loss_of_senses;
    private CheckBox nota1;
    private CheckBox diabetes;
    private CheckBox hypertension;
    private CheckBox lung_disease;
    private CheckBox heart_disease;
    private CheckBox kidney_disorder;
    private CheckBox nota2;
    private CheckBox recently_interacted;
    private CheckBox healthcare_worker;
    private CheckBox nota3;
    private RadioGroup radioGroup;
    private RadioButton yes;
    private RadioButton no;
    private Button submit;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tester);

        cough = findViewById(R.id.cough);
        fever = findViewById(R.id.fever);
        difficulty_in_breathing = findViewById(R.id.difficulty_in_breathing);
        loss_of_senses = findViewById(R.id.loss_of_senses);
        nota1 = findViewById(R.id.nota1);
        diabetes = findViewById(R.id.diabetes);
        hypertension = findViewById(R.id.hypertension);
        lung_disease = findViewById(R.id.lung_disease);
        heart_disease = findViewById(R.id.heart_disease);
        kidney_disorder = findViewById(R.id.kidney_disorder);
        nota2 = findViewById(R.id.nota2);
        recently_interacted = findViewById(R.id.recently_interacted);
        healthcare_worker = findViewById(R.id.healthcare_worker);
        nota3 = findViewById(R.id.nota3);
        radioGroup = findViewById(R.id.radiogroup1);
        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);
        submit = findViewById(R.id.submit);
        result = findViewById(R.id.result);

        backbutton = findViewById(R.id.backbutton);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backbutton();
            }
        });

        cough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cough.isChecked() || fever.isChecked() || difficulty_in_breathing.isChecked() || loss_of_senses.isChecked()){
                    nota1.setEnabled(false);
                }else{
                    nota1.setEnabled(true);
                }
            }
        });

        fever.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cough.isChecked() || fever.isChecked() || difficulty_in_breathing.isChecked() || loss_of_senses.isChecked()){
                    nota1.setEnabled(false);
                }else{
                    nota1.setEnabled(true);
                }
            }
        });

        difficulty_in_breathing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cough.isChecked() || fever.isChecked() || difficulty_in_breathing.isChecked() || loss_of_senses.isChecked()){
                    nota1.setEnabled(false);
                }else{
                    nota1.setEnabled(true);
                }
            }
        });

        loss_of_senses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cough.isChecked() || fever.isChecked() || difficulty_in_breathing.isChecked() || loss_of_senses.isChecked()){
                    nota1.setEnabled(false);
                }else{
                    nota1.setEnabled(true);
                }
            }
        });

        nota1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nota1.isChecked()){
                    cough.setEnabled(false);
                    fever.setEnabled(false);
                    difficulty_in_breathing.setEnabled(false);
                    loss_of_senses.setEnabled(false);
                }else{
                    cough.setEnabled(true);
                    fever.setEnabled(true);
                    difficulty_in_breathing.setEnabled(true);
                    loss_of_senses.setEnabled(true);
                }
            }
        });

        diabetes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(diabetes.isChecked() || hypertension.isChecked() || lung_disease.isChecked() || heart_disease.isChecked() || kidney_disorder.isChecked()){
                    nota2.setEnabled(false);
                }else{
                    nota2.setEnabled(true);
                }
            }
        });

        hypertension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(diabetes.isChecked() || hypertension.isChecked() || lung_disease.isChecked() || heart_disease.isChecked() || kidney_disorder.isChecked()){
                    nota2.setEnabled(false);
                }else{
                    nota2.setEnabled(true);
                }
            }
        });

        lung_disease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(diabetes.isChecked() || hypertension.isChecked() || lung_disease.isChecked() || heart_disease.isChecked() || kidney_disorder.isChecked()){
                    nota2.setEnabled(false);
                }else{
                    nota2.setEnabled(true);
                }
            }
        });

        heart_disease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(diabetes.isChecked() || hypertension.isChecked() || lung_disease.isChecked() || heart_disease.isChecked() || kidney_disorder.isChecked()){
                    nota2.setEnabled(false);
                }else{
                    nota2.setEnabled(true);
                }
            }
        });

        kidney_disorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(diabetes.isChecked() || hypertension.isChecked() || lung_disease.isChecked() || heart_disease.isChecked() || kidney_disorder.isChecked()){
                    nota2.setEnabled(false);
                }else{
                    nota2.setEnabled(true);
                }
            }
        });

        nota2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nota2.isChecked()){
                    diabetes.setEnabled(false);
                    hypertension.setEnabled(false);
                    lung_disease.setEnabled(false);
                    heart_disease.setEnabled(false);
                    kidney_disorder.setEnabled(false);
                }else{
                    diabetes.setEnabled(true);
                    hypertension.setEnabled(true);
                    lung_disease.setEnabled(true);
                    heart_disease.setEnabled(true);
                    kidney_disorder.setEnabled(true);
                }
            }
        });

        recently_interacted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recently_interacted.isChecked() || healthcare_worker.isChecked()){
                    nota3.setEnabled(false);
                }else{
                    nota3.setEnabled(true);
                }
            }
        });

        healthcare_worker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recently_interacted.isChecked() || healthcare_worker.isChecked()){
                    nota3.setEnabled(false);
                }else{
                    nota3.setEnabled(true);
                }
            }
        });

        nota3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nota3.isChecked()){
                    recently_interacted.setEnabled(false);
                    healthcare_worker.setEnabled(false);
                }else{
                    recently_interacted.setEnabled(true);
                    healthcare_worker.setEnabled(true);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nota1.isChecked() && nota2.isChecked() && no.isChecked() && nota3.isChecked()){
                    result.setText(R.string.low_risk);
                }else if (cough.isChecked() || fever.isChecked() || difficulty_in_breathing.isChecked() || loss_of_senses.isChecked() || diabetes.isChecked() || hypertension.isChecked() || lung_disease.isChecked() || heart_disease.isChecked() || kidney_disorder.isChecked() || recently_interacted.isChecked() || healthcare_worker.isChecked()){
                    result.setText(R.string.high_risk);
                }else{
                    result.setText(R.string.not_answered);
                }
            }
        });
    }

    public void backbutton(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
