package com.meshyog.emptycan.controller;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.AppConstants;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.JsonObject;
import com.meshyog.emptycan.R;
import com.meshyog.emptycan.model.AppConfig;
import com.meshyog.emptycan.model.AppUtils;
import com.meshyog.emptycan.model.ScalingUtilities;
import com.meshyog.emptycan.model.database.EmptycanDataBase;
import com.meshyog.emptycan.model.server.RetrofitAdaptor;
import com.meshyog.emptycan.model.server.WebServiceInterface;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

/**
 * Created by Viswanathan on 12/10/16.
 */
public class UserProfileActivity extends AppCompatActivity {


    public SharedPreferences sharedPreferences;
    public   String userName;
    public   String dob;
    public   String emailId;
    public   String gender;
    public String imageUrl;
    public TextView addressEdit;
    public NetworkImageView userProfileImageView;
    int PICK_PHOTO_FOR_AVATAR=10;
    String BUCKETNAME="virranbi.appspot.com";
    ImageLoader imageLoader =null;
    public InputStream UploadFileStream=null;
    Drawable loaderDrawable=null;
    Animation animZoomIn;
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        loaderDrawable= getApplicationContext().getResources().getDrawable(R.drawable.ic_sync_drawable);

        try{
            if(getSupportActionBar()!=null){
                getSupportActionBar().setTitle("Profile");
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
            imageLoader = AppConfig.getInstance().getImageLoader();
            TextView fullName=  (TextView)findViewById(R.id.consumerFullName);
            TextView consumerGender=  (TextView)findViewById(R.id.consumerGender);
            TextView consumerDob=  (TextView)findViewById(R.id.consumerDob);
            TextView consumerEmailId=  (TextView)findViewById(R.id.consumerEmailId);
            TextView  basicProfileEdit= (TextView)findViewById(R.id.basicProfileEdit);
            TextView userDefaultAddress=(TextView)findViewById(R.id.userDefaultAddress);
            userProfileImageView=(NetworkImageView)findViewById(R.id.profile_image);
            animZoomIn = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.zoom_in);
             addressEdit= (TextView)findViewById(R.id.AddrssEdit);
            this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            userName= sharedPreferences.getString(EmptycanDataBase.USER_NAME,"FirstName LastName");
            dob= sharedPreferences.getString(EmptycanDataBase.USER_DOB,"00-00-0000");
            emailId= sharedPreferences.getString(EmptycanDataBase.USER_EMAIL,"noemail@xxxx.com");
            gender= sharedPreferences.getString(EmptycanDataBase.USER_GENDER,"male/female/transgender");
            imageUrl= sharedPreferences.getString(EmptycanDataBase.USER_PRO_IMG_URL,AppConstants.USER_DEFAULT_AVATHAR);
            userProfileImageView.setOnClickListener(listener);
            userProfileImageView.setImageUrl(imageUrl,imageLoader);
            userProfileImageView.setImageDrawable(loaderDrawable);
           // frameAnimation = (AnimationDrawable) userProfileImageView.getDrawable();
            fullName.setText(userName);
            consumerGender.setText(gender);
            consumerDob.setText(dob);
            consumerEmailId.setText(emailId);
            basicProfileEdit.setOnClickListener(listener);
            addressEdit.setOnClickListener(listener);
            String addressInfo=  this.sharedPreferences.getString("defaultAddress","");
            String nodefaultAddress=  this.sharedPreferences.getString("nodefaultAddress","");

            if(!addressInfo.equals("")){
                JSONObject jsonObject=new JSONObject(addressInfo);
                String locationName=  jsonObject.getString("locationName");
                userDefaultAddress.setText(locationName);
                addressEdit.setText("View Address");
            }else if(!nodefaultAddress.equals("")){
                JSONObject jsonObject=new JSONObject(nodefaultAddress);
                String locationName=  jsonObject.getString("locationName");
                userDefaultAddress.setText(locationName);
                addressEdit.setText("View Address");
            }else{
                userDefaultAddress.setText("No Address Saved Yet.");
                addressEdit.setText("Add Address");
            }
            //imageView=(ImageView)findViewById(R.id.profile_image);
            userProfileImageView.setOnClickListener(listener);

            /*Resources resources=getResources();
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inPreferredConfig= Bitmap.Config.RGB_565;
            Bitmap source= BitmapFactory.decodeResource(resources,R.drawable.dog,options);
            RoundedBitmapDrawable drawable= RoundedBitmapDrawableFactory.create(resources,source);
            drawable.setCornerRadius(Math.max(source.getWidth(),source.getHeight())/2f);
            drawable.setCircular(true);
            imageView.setImageDrawable(drawable);*/
            mShortAnimationDuration = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);
        }catch(Exception e){
            e.printStackTrace();
        }



    }
    public  void showImageLoader(String status){

      /*  if(status.equals("start"))
        frameAnimation.start();
        else if(status.equals("stop"))
         frameAnimation.stop();*/

    }
    private View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.basicProfileEdit:
                    Intent myIntent = new Intent(getApplicationContext(), BasicProfileEditActivity.class);
                    myIntent.putExtra(EmptycanDataBase.USER_NAME,userName);
                    myIntent.putExtra(EmptycanDataBase.USER_EMAIL,emailId);
                    myIntent.putExtra(EmptycanDataBase.USER_DOB,dob);
                    myIntent.putExtra(EmptycanDataBase.USER_GENDER,gender);
                    startActivity(myIntent);
                    finish();
                    break;

                case R.id.AddrssEdit:
                    String option= addressEdit.getText().toString();
                    if(option.equals("View Address")){
                        Intent addressListIntent = new Intent(getApplicationContext(), AddressListActivity.class);
                        startActivity(addressListIntent);
//                        finish();
                    }else if(option.equals("Add Address")){
                        Intent addressListIntent = new Intent(getApplicationContext(), AddressFormActivity.class);
                        startActivity(addressListIntent);
                        //finish();
                    }


                    // onBackPressed();
                    break;
                case R.id.profile_image:
                    userProfileImageView.startAnimation(animZoomIn);
                    //zoomImageFromThumb(view,userProfileImageView);
                   /* Intent intent = new Intent();
                    intent.setType("image*//*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PHOTO_FOR_AVATAR);*/
                    break;
            }
        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }
    private void zoomImageFromThumb(final View thumbView, final NetworkImageView expandedImageView) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
       /* final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);*/
        /*expandedImageView.setImageResource(imageResId);*/

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id ==android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK && null != data) {
            try{
                if (data == null) {
                    //Display an error
                    return;
                }
                UploadFileStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
                File file=null;
                if(android.os.Build.VERSION.SDK_INT >= 20){
                    String tempFileName=decodeFile(getRealPathFromURI_BelowAPI11(getApplicationContext(),data.getData()),500,500);
                    file=new File(tempFileName);
                }else if(android.os.Build.VERSION.SDK_INT <= 19){
                    String tempFileName=decodeFile(getRealPathFromURI_BelowAPI11(getApplicationContext(),data.getData()),500,500);
                    file= new File(tempFileName);
                }


              /*  File file=new File(inputStream);
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA }*/;

                /*Cursor cursor = getContentResolver().query(,
                        filePathColumn, null, null, null);selectedImage
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                System.out.println(picturePath);
                File file=new File(picturePath);
                file.getAbsoluteFile();
                OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                getResizedBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()),150,150);
                FileInputStream fileInputStream =new FileInputStream(file);
                os.close();*/
                //uploadImage(file);
                //uploadFile(BUCKETNAME,"testfile",picturePath);
                RemoteDataTask  remoteDataTask= new RemoteDataTask(BUCKETNAME,"emptycanUsersImage/"+String.valueOf(new EmptycanDataBase(getApplicationContext()).getConsumerKey()),UploadFileStream,file) ;
                remoteDataTask.execute();
                userProfileImageView.setImageDrawable(loaderDrawable);
                //sendImageToServer();

            }catch(Exception e){
                    e.printStackTrace();
            }

            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }
    public void uploadImage(File avatharImageFile){

        if (AppUtils.isNetworkAvailable(this)) {
            //consumerJson=new JsonObject();
            try{
                Bitmap bmp= BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.dog);
                Retrofit retrofit =null;
                WebServiceInterface webServiceInterface=null;
                retrofit = RetrofitAdaptor.getRetrofit();
                webServiceInterface = retrofit.create(WebServiceInterface.class);
                // File file = new File("");
               /* RequestBody fbody = RequestBody.create(MediaType.parse("image*//*"), byteArray);
                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "");
                RequestBody id = RequestBody.create(MediaType.parse("text/plain"), "");*/
                RequestBody requestNombres =
                        RequestBody.create(
                                MediaType.parse("image/jpeg"), avatharImageFile);
                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "VARADHARAJABN");
                //MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", avatharImageFile.getName(), RequestBody.create(MediaType.parse("image/jpeg"), requestNombres));
                MultipartBody.Part  filePart = MultipartBody.Part.createFormData("file", avatharImageFile.getName(), requestNombres);
                MultipartBody.Part  consumerKeyPart = MultipartBody.Part.createFormData("consumerKey", String.valueOf(new EmptycanDataBase(getApplicationContext()).getConsumerKey()), name);
                JsonObject result=new JsonObject();
                result.addProperty("consumerKey",new EmptycanDataBase(getApplicationContext()).getConsumerKey());
               /* RequestBody requestPassword =
                        RequestBody.create(
                                MediaType.parse("multipart/form-data"), password);*/
                RequestBody consumerKey =
                        RequestBody.create(
                                MediaType.parse("application/json"), result.toString());

                Call<JsonObject> call = webServiceInterface.uploadImage(filePart,new EmptycanDataBase(getApplicationContext()).getConsumerKey());
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(retrofit.Response<JsonObject> response, Retrofit retrofit) {
                        if(response.message().equals("OK")){
                            Toast.makeText(getApplicationContext(),"File uploaded",Toast.LENGTH_SHORT).show();
                        }
                        //AZUtils.printObject(response.body());
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                });
             }catch(Exception e){
                e.printStackTrace();
//                progressDialog.dismiss();
            }


        } else {
           // mAlertDaiog("Check your internet connectivity", false);
        }

    }
   /* static Storage storage = null;
    private static Storage getStorage() throws Exception {

        if (storage == null) {
            HttpTransport httpTransport = new NetHttpTransport();
            JsonFactory jsonFactory = new JacksonFactory();
            List<String> scopes = new ArrayList<String>();
            scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);

            Credential credential = new GoogleCredential.Builder()
                    .setTransport(httpTransport)
                    .setJsonFactory(jsonFactory)
                    .setServiceAccountId("virranbi@appspot.gserviceaccount.com") //Email
                    .setServiceAccountPrivateKeyFromP12File(getTempPkc12File())
                    .setServiceAccountScopes(scopes).build();

            storage = new Storage.Builder(httpTransport, jsonFactory,
                    credential).setApplicationName("EMPTYCAN")
                    .build();
        }

        return storage;
    }

    private static File getTempPkc12File() throws IOException {
        // xxx.p12 export from google API console
        InputStream pkc12Stream = AppConfig.getInstance().getAssets().open("virranbi-c752cd1ad6e7.p12");
        File tempPkc12File = File.createTempFile("temp_pkc12_file", "p12");
        OutputStream tempFileStream = new FileOutputStream(tempPkc12File);
        int read = 0;
        byte[] bytes = new byte[1024];
        while ((read = pkc12Stream.read(bytes)) != -1) {
            tempFileStream.write(bytes, 0, read);
        }
        return tempPkc12File;
    }*/



    public class RemoteDataTask extends AsyncTask<String, String, String> {

        private String bucketName;
        private String name;
       // private String filePath;
        private InputStream file;
        private File profilePic;
        public RemoteDataTask(String bucketNameParam,String nameParam,InputStream filePathParam,File file ) {
            this.bucketName = bucketNameParam;
            this.name = nameParam;
            this.file = filePathParam;
            this.profilePic=file;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            // UserSignUpActivity.this.resp_msg = SignUpActivity.this.getResources().getString(C0609R.string.went_wrong);
        }

       /* @Override
        protected String doInBackground(String... strings) {
            InputStream stream=null;
            try {
            Storage storage = getStorage();
            StorageObject object = new StorageObject();
            object.setBucket(bucketName);

            // File sdcard = Environment.getExternalStorageDirectory();
            //File file = new File(sdcard,filePath);
            //File file = new File(filePath);
            //stream = new FileInputStream(file);
                //storage.objects().
                InputStreamContent content = new InputStreamContent("image/jpeg",file);
                Storage.Objects.Insert insert = storage.objects().insert(bucketName, null, content);
                insert.setName(name);

                StorageObject obj = insert.execute();
                //obj.get
                Log.d("UserProfileImageUpload", obj.getSelfLink());
               // stream.close();
                file.close();;
               // Toast.makeText(getApplicationContext(),"Image uploaded successfully",Toast.LENGTH_SHORT).show();
            }catch(Exception  e){
                e.printStackTrace();
               // Toast.makeText(getApplicationContext(),"Image Failled to upload successfully",Toast.LENGTH_SHORT).show();
                //e.printStackTrace();
            }
            finally {


            }

            return "result";
        }*/
       protected String doInBackground(String... strings) {

//start animation
           showImageLoader("start");

           String url=sendImageToServer(this.profilePic,new EmptycanDataBase(getApplicationContext()).getConsumerKey());
           return url;
       }
        protected void onPostExecute(String resultFromServer) {
            try {
                UploadFileStream.close();
            if(resultFromServer!=null){
                JSONObject jsonObject=new JSONObject(resultFromServer);
                if(!resultFromServer.equals("") && jsonObject.get("status").equals("success")){

                    String imgBloburl=jsonObject.getString("servingUrl")+"=s192-c";
                    imageLoader.get(imgBloburl, new ImageLoader.ImageListener() {

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }

                        @Override
                        public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                            Bitmap bitmap= imageContainer.getBitmap();
                            if (bitmap != null) {
                                userProfileImageView.setImageBitmap(bitmap);
                                showImageLoader("stop");
                            }
                        }
                    });
                   // userProfileImageView.setImageUrl(imgBloburl,imageLoader);
                    //userProfileImageView.set

                    SharedPreferences.Editor editor= sharedPreferences.edit();
                    editor.putString(EmptycanDataBase.USER_PRO_IMG_URL,imgBloburl);
                    editor.commit();
                    editor.commit();
                    Toast.makeText(getApplicationContext(),"Image uploaded successfully",Toast.LENGTH_SHORT).show();
                }else{
                    userProfileImageView.setImageUrl(AppConstants.USER_DEFAULT_AVATHAR,imageLoader);
                    Toast.makeText(getApplicationContext(),"Image uploaded Faillure",Toast.LENGTH_SHORT).show();
                }
            }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    public static Bitmap getResizedBitmap(Bitmap image, int newHeight, int newWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }

    public String sendImageToServer(File avatharImageFile,long consumerKey){
       String result="";
        try{
            avatharImageFile=saveBitmapToFile(avatharImageFile);
           HttpClient httpclient = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(httpclient.getParams(), 10000); //Timeout Limit

            HttpGet httpGet = new HttpGet(AppConstants.dummybaseContext+"/get-blob-upload-url");
            HttpResponse response = httpclient.execute(httpGet);
           // System.out.println(response);
           String uploadUrlReturned= EntityUtils.toString(response.getEntity());
            System.out.println(uploadUrlReturned);
            /*RequestBody requestNombres =
                    RequestBody.create(
                            MediaType.parse("image/jpeg"), avatharImageFile);*/
            HttpClient httpclient2 = new DefaultHttpClient();
           // uploadUrlReturned= uploadUrlReturned.replace("LAPTOP-DR0SFJF2","192.168.0.117");
            HttpPost httppost = new HttpPost(uploadUrlReturned);
            FileBody fileBody = new FileBody(avatharImageFile, ContentType.create("image/jpeg", Consts.UTF_8),String.valueOf(consumerKey)); //image should be a String

          //  FileBody fileBody  = new FileBody(thumbnailFile);
            MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
            reqEntity.addPart("file",fileBody);
            reqEntity.addTextBody("consumerKey",String.valueOf(consumerKey));
            //reqEntity.addPart("file", requestNombres);
            httppost.setEntity(reqEntity.build());
            HttpResponse response2 = httpclient2.execute(httppost);
            result= EntityUtils.toString(response2.getEntity());
            // System.out.println(result);
        }catch (Exception e){
            e.printStackTrace();
        }

return result;
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


    public File saveBitmapToFile(File file){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=500;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if(cursor != null){
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    public static String getRealPathFromURI_BelowAPI11(Context context, Uri contentUri){
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index
                = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
    private String decodeFile(String path,int DESIREDWIDTH, int DESIREDHEIGHT) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
            } else {
                unscaledBitmap.recycle();
                return path;
            }

            // Store to tmp file

            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/TMMFOLDER");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }

            String s = "tmp.png";

            File f = new File(mFolder.getAbsolutePath(), s);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }
}
