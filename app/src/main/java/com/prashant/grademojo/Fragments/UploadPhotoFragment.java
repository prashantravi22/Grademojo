package com.prashant.grademojo.Fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.prashant.grademojo.Helpers.FilePath;
import com.prashant.grademojo.Helpers.VolleyHelper;
import com.prashant.grademojo.LocalDbManager;
import com.prashant.grademojo.R;

import net.gotev.uploadservice.BinaryUploadRequest;
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.MultipartUploadTask;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class UploadPhotoFragment extends Fragment {


    View view;
    Button choosebtn, uploadbtn;
    VolleyHelper volleyHelper;
    ImageView viewImage,successimage,selectpdf;
    TextView textView;
    public String picturePath = "";
    public Uri filePath1;
    static int requestcodeupload;
    FloatingActionButton fb;
    FloatingActionMenu fm;
    String[] namefilter={"Choose from Gallery", "Attach pdf"};
    ArrayList<FloatingActionButton> floatingActionButtonArrayList = new ArrayList<FloatingActionButton>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_upload_photo, container, false);
        choosebtn = (Button) view.findViewById(R.id.selet_photo);
        viewImage = (ImageView) view.findViewById(R.id.viewImage);
        successimage = (ImageView) view.findViewById(R.id.successimage);
        selectpdf = (ImageView) view.findViewById(R.id.pdf_selected);
        textView = (TextView) view.findViewById(R.id.photopath);
        volleyHelper = VolleyHelper.getRestHelper(getActivity());
        getActivity().setTitle("Upload file");
        choosebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectImage();
            }
        });
        uploadbtn = (Button) view.findViewById(R.id.upload_pic);
        if (LocalDbManager.getToken(getContext().getApplicationContext()) == "" || (LocalDbManager.getToken(getContext().getApplicationContext()).equals("") || LocalDbManager.getToken(getContext().getApplicationContext()) == null)) {
            uploadbtn.setVisibility(View.GONE);
            Snackbar.make(view, "Please Access Token" + LocalDbManager.getToken(getContext().getApplicationContext()), Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(view, "Attach file", Snackbar.LENGTH_LONG).show();
        }
        setHasOptionsMenu(true);
        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                File file=new File(picturePath);
                long length = file.length();
                length = length/1024;
               // System.out.println("File Path :  " + file.getPath() + ", File size : " + length +" KB");
              //  Toast.makeText(getActivity(),"*** length"+length, Toast.LENGTH_LONG).show();
                if (picturePath == "") {
                    Snackbar.make(view, "Empty Attach file", Snackbar.LENGTH_LONG).show();
                }
                else if(length>=15360)
                {
                    Snackbar.make(view, "Please upload less then 15MB ", Snackbar.LENGTH_LONG).show();
                }
                else if(picturePath !="" && length<15360){
                    uploadimage(getActivity());
                }

            }
        });

        fm = (FloatingActionMenu)view.findViewById(R.id.menu_item);

        for (int i = 0; i < namefilter.length; i++) {
            fb = new FloatingActionButton(getActivity());
            fb.setImageDrawable(getResources().getDrawable(R.drawable.pdf));
            fb.setLabelText(namefilter[i]);
            fb.setColorNormal(getResources().getColor(R.color.colorAccentblue));
            fb.setColorPressed(getResources().getColor(R.color.colorPrimaryDark));
            fb.setButtonSize(FloatingActionButton.SIZE_MINI);
            fb.setClickable(true);

            floatingActionButtonArrayList.add(fb);

            final int finalI = i;
            fb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(finalI==0)
                    {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 1);
                    }
                    else if(finalI==1)
                    {
                        Intent intent = new Intent();
                        intent.setType("application/pdf");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), 2);
                    }
                    Toast.makeText(getActivity(), finalI + "", Toast.LENGTH_LONG).show();

                }
            });
            fm.addMenuButton(fb);
        }


        return view;
    }

    private void selectImage() {

        final CharSequence[] options = {"Choose from Gallery", "Attach pdf", "Cancel"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);

                } else if (options[item].equals("Attach pdf")) {
                    Intent intent = new Intent();
                    intent.setType("application/pdf");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Pdf"), 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                requestcodeupload = 1;
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                selectpdf.setVisibility(View.GONE);
                viewImage.setVisibility(View.VISIBLE);
                successimage.setVisibility(View.GONE);
                int columnIndex = c.getColumnIndex(filePath[0]);
                picturePath = c.getString(columnIndex);

                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                textView.setText(picturePath);
                viewImage.setImageBitmap(thumbnail);
            } else if (requestCode == 2) {
                if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
                    filePath1 = data.getData();
                    requestcodeupload = 2;
                    selectpdf.setVisibility(View.VISIBLE);
                    viewImage.setVisibility(View.GONE);
                    successimage.setVisibility(View.GONE);
                    picturePath = FilePath.getPath(getActivity(), filePath1);
                }
            }
        }
    }

    public void uploadimage(Context context) {

        String uploadId = UUID.randomUUID().toString();
        try {
            MultipartUploadRequest multipartUploadRequest = new MultipartUploadRequest(context, uploadId, "https://www.grademojo.com/api/interview/file/upload");
            if (requestcodeupload == 1) {

                multipartUploadRequest.addFileToUpload(picturePath, "attached_file");

                selectpdf.setVisibility(View.GONE);
                viewImage.setVisibility(View.GONE);
                successimage.setVisibility(View.VISIBLE);

            } else if (requestcodeupload == 2) {

                multipartUploadRequest.addFileToUpload(picturePath, "attached_file");
                selectpdf.setVisibility(View.GONE);
                viewImage.setVisibility(View.GONE);
                successimage.setVisibility(View.VISIBLE);
            }
            multipartUploadRequest.addParameter("access_token", LocalDbManager.getToken(getContext().getApplicationContext()));
            multipartUploadRequest.setNotificationConfig(new UploadNotificationConfig());
            multipartUploadRequest.setMaxRetries(2);
            multipartUploadRequest.startUpload();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();

        getActivity().getMenuInflater().inflate(R.menu.upload, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.upload) {

            File file=new File(picturePath);
            long length = file.length();
            length = length/1024;
            // System.out.println("File Path :  " + file.getPath() + ", File size : " + length +" KB");
            //  Toast.makeText(getActivity(),"*** length"+length, Toast.LENGTH_LONG).show();
            if (picturePath == "") {
                Snackbar.make(view, "Empty Attach file", Snackbar.LENGTH_LONG).show();
            }
            else if(length>=15360)
            {
                Snackbar.make(view, "Please upload less then 15MB ", Snackbar.LENGTH_LONG).show();
            }
            else if(LocalDbManager.getToken(getContext().getApplicationContext()) == "")
            {
                Snackbar.make(view, "Please Access Token ", Snackbar.LENGTH_LONG).show();
            }
            else if(picturePath !="" && length<15360 && LocalDbManager.getToken(getContext().getApplicationContext()) != ""){
                uploadimage(getActivity());
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
