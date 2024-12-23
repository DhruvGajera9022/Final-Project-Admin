package com.example.electrohub;

import static android.content.Intent.ACTION_PICK;

import static java.security.AccessController.getContext;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Add_Product_Activity extends AppCompatActivity {

    ImageButton back;
    ImageView imgview1,imgview2,imgview3,selectedImageView;
    Uri imguri;
    Spinner spin_cate,spin_com;
    private ArrayList<Uri> fileUris = new ArrayList<>();
    Button btn_clear,btn_inert,btn_cancel;
    EditText edt_product_name,edt_product_price,edt_product_quantity,edt_product_desc;

    String sel_company,sel_cate;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_add_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //image button
        back = findViewById(R.id.back_for_addproduct);

        //Imageview
        imgview1 = findViewById(R.id.input_image_1);
        imgview2 = findViewById(R.id.input_image_2);
        imgview3 = findViewById(R.id.input_image_3);

        //Spinner
        spin_cate = findViewById(R.id.spinner_category);
        spin_com = findViewById(R.id.spinner_company);

        //Button
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_inert = findViewById(R.id.btn_insert);
        btn_clear = findViewById(R.id.btn_clear);

        //EditText
        edt_product_name = findViewById(R.id.edt_product_name);
        edt_product_price = findViewById(R.id.edt_product_price);
        edt_product_quantity = findViewById(R.id.edt_product_quantity);
        edt_product_desc = findViewById(R.id.edt_product_description);

        //firestore reference

        //On click of imageviews
        imgview1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker(imgview1);
            }
        });

        imgview2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker(imgview2);
            }
        });

        imgview3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker(imgview3);
            }
        });


        //on click of image button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Spinner for Category
        ArrayAdapter<CharSequence> adapter_category = ArrayAdapter.createFromResource(
                this,
                R.array.category,
                R.layout.custom_spinner_layout
        );
        adapter_category.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spin_cate.setAdapter(adapter_category);

        spin_cate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sel_cate =parent.getItemAtPosition(position).toString();
                spin_com.setVisibility(View.VISIBLE);
                spin_com.setAdapter(set_adapter(sel_cate));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Spinner for Company
        spin_com.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sel_company =parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //button clear
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgview1.setImageResource(R.drawable.baseline_add_photo_alternate_24);
                imgview2.setImageResource(R.drawable.baseline_add_photo_alternate_24);
                imgview3.setImageResource(R.drawable.baseline_add_photo_alternate_24);

                edt_product_name.setText("");
                edt_product_price.setText("");
                edt_product_quantity.setText("");
                edt_product_desc.setText("");

                spin_cate.setSelection(0);
            }
        });

        //button cancel
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_inert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (fileUris.isEmpty()) {
                    showCustomToast("Please Select At least One Image", R.drawable.baseline_arrow_back_24);
                    return;
                }

                ProgressDialog progressDialog = new ProgressDialog(Add_Product_Activity.this);
                progressDialog.setTitle("Wait While Data Inserting...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                fileUris.clear(); // Clear previous data
                if (imgview1.getTag() != null) fileUris.add((Uri) imgview1.getTag());
                if (imgview2.getTag() != null) fileUris.add((Uri) imgview2.getTag());
                if (imgview3.getTag() != null) fileUris.add((Uri) imgview3.getTag());

                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss", Locale.UK);
                String timestamp = format.format(new Date());
                ArrayList<String> imageUrls = new ArrayList<>();

                // Use secure methods to retrieve Cloudinary credentials
                Map<String, String> config = new HashMap<>();
                config.put("cloud_name", "dbbdbt7z1");
                config.put("api_key", "731466623192577");
                config.put("api_secret", "C9mFzlUvIQCzbzumNK7C0hz1gHo");
                Cloudinary cloudinary = new Cloudinary(config);

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    for (Uri uri : fileUris) {
                        try (InputStream inputStream = getContentResolver().openInputStream(uri)) {
                            if (inputStream != null) {
                                Map uploadResult = cloudinary.uploader().upload(inputStream, ObjectUtils.emptyMap());
                                String imageUrl = (String) uploadResult.get("secure_url");
                                imageUrls.add(imageUrl);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> showCustomToast("Error uploading image", R.drawable.baseline_arrow_back_24));
                        }
                    }

                    // Proceed to save data to Firestore
                    runOnUiThread(() -> {
                        String documentId = db.collection("Products").document().getId();
                        Map<String, Object> map = new HashMap<>();
                        map.put("imgurls", imageUrls);
                        map.put("name", edt_product_name.getText().toString());
                        map.put("price", edt_product_price.getText().toString());
                        map.put("quantity", edt_product_quantity.getText().toString());
                        map.put("description", edt_product_desc.getText().toString());
                        map.put("category", sel_cate);
                        map.put("company", sel_company);
                        map.put("pid",documentId);

                        db.collection("Products").document(documentId)
                                .set(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        imgview1.setImageResource(R.drawable.baseline_add_photo_alternate_24);
                                        imgview2.setImageResource(R.drawable.baseline_add_photo_alternate_24);
                                        imgview3.setImageResource(R.drawable.baseline_add_photo_alternate_24);

                                        edt_product_name.setText("");
                                        edt_product_price.setText("");
                                        edt_product_quantity.setText("");
                                        edt_product_desc.setText("");

                                        spin_cate.setSelection(0);

                                        progressDialog.dismiss();
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Add_Product_Activity.this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    });
                });
            }
        });

    }

    private void showCustomToast(String message, int iconResId) {
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast_layout, null);

        // Set the text and icon
        TextView toastMessage = layout.findViewById(R.id.toast_message);

        ImageView toastIcon = layout.findViewById(R.id.toast_icon);
        toastMessage.setText(message);
        toastIcon.setImageResource(iconResId);

        // Create the toast
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //adapter function for selected category
    private ArrayAdapter<CharSequence> set_adapter(String categoryTitle) {
        int arrayResource;

        // Map categoryTitle to the appropriate string-array resource
        switch (categoryTitle.toLowerCase()) {
            case "mobile":
                arrayResource = R.array.Mobile;
                break;
            case "tv":
                arrayResource = R.array.TV;
                break;
            case "laptop":
                arrayResource = R.array.Laptop;
                break;
            case "headphone":
                arrayResource = R.array.Headphone;
                break;
            case "airbuds":
                arrayResource = R.array.AirBuds;
                break;
            case "speaker":
                arrayResource = R.array.Speaker;
                break;
            case "keyboard":
                arrayResource = R.array.Keyboard;
                break;
            case "mouse":
                arrayResource = R.array.Mouse;
                break;
            case "camera":
                arrayResource = R.array.Camera;
                break;
            case "smartwatch":
                arrayResource = R.array.Smartwatch;
                break;
            case "tablet":
                arrayResource = R.array.Tablet;
                break;
            default:
                throw new IllegalArgumentException("Unknown category: " + categoryTitle);
        }

        // Create and return the adapter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                arrayResource,
                R.layout.custom_spinner_layout
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }

    //function for image picker
    private void openImagePicker(ImageView imageView) {
        selectedImageView = imageView; // Keep track of which ImageView was clicked
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent,100);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null && selectedImageView != null) {
                selectedImageView.setImageURI(imageUri);
                selectedImageView.setTag(imageUri);
            }
        }
    }
}