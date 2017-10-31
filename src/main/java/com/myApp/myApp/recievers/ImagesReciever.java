package com.myApp.myApp.recievers;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Created by Anton on 13-Oct-17.
 */
public class ImagesReciever {

    @Value("${com.cloudinary.cloud_name}")
    String mCloudName;

    @Value("${com.cloudinary.api_key}")
    String mApiKey;

    @Value("${com.cloudinary.api_secret}")
    String mApiSecret;

    public ImagesReciever() {}

    public List<String> uploadImages (List<MultipartFile> files) {
        return files.stream().map(file -> getImageUrl(file)).collect(toList());
    }

    public String getImageUrl (MultipartFile file) {
        Cloudinary cloudinary = new Cloudinary("cloudinary://"+mApiKey+":"+mApiSecret+"@"+mCloudName);
        String string = "";
        if (file.isEmpty()){
            return string;
        }
        try {
            File f= Files.createTempFile("temp", file.getOriginalFilename()).toFile();
            file.transferTo(f);
            Map response = cloudinary.uploader().upload(f, ObjectUtils.emptyMap());
            JSONObject json=new JSONObject(response);

            String urlCropped = cloudinary.url().format("jpg")
                    .transformation(new Transformation().width(250).height(168).crop("fit"))
                    .generate();

            return json.getString("url");

        } catch (IOException e){
            e.printStackTrace();
        }
        return string;
    }
}
