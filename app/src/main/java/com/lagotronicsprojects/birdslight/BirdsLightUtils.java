package com.lagotronicsprojects.birdslight;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class BirdsLightUtils {
    public static final int WRITE_PERMISSION = 0;
    public int trialCounter;

    Context _app;
    private String _password;
    private String _alias;
    private Key _key;
    private int _counter;
    private String _content;
    private File _directory = new File(Environment.getExternalStorageDirectory().getPath() + "/.BL/"); //fixed
    private File _keyChain = new File(_directory,"key.txt");
    private File _data = new File(_directory, "data.txt");
    private KeyStore _ks;
    private JSONObject _jo;

    BirdsLightUtils (Context app,@Nullable String alias, @Nullable String password) {
        _app = app;
        _password = password;
        _alias = alias;

        try {
            _ks = KeyStore.getInstance(KeyStore.getDefaultType());
            _ks.load(null,_password.toCharArray());

        } catch (Exception e) {
            Log.e("KS", "KS was Already Initialized");
        }

        if (!_directory.isDirectory()){
            _directory.mkdir();
        }

        if (!_keyChain.exists()) {
            try {
                _keyChain.createNewFile();
                //alias is required
                this.createKey();
            } catch (Exception e) {
                Log.e("File",e.toString()  + "which is Key File");
            }
        } else {
            //get the currentkey.
            this.getKey();
        }
        if (!_data.exists()) {
            try {
                _data.createNewFile();
            } catch (Exception e) {
                Log.e("File", e.toString() + "which is Data File");
            }
        }
    }


    private boolean getKey() {
        try {

            try (FileInputStream fis2 = new FileInputStream(_keyChain)) {
                _ks.load(fis2, _password.toCharArray());
            }
            _key = _ks.getKey(_alias, _password.toCharArray());

            return true;
        } catch (Exception e) {
            Log.e("KS", e.toString());
            return false;
        }
    }

    private void createKey() {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            SecretKey k = keygen.generateKey();
            _key = k;
            KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(_password.toCharArray());
            KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(k);
            _ks.setEntry(_alias, skEntry, protParam);
            try (FileOutputStream os = new FileOutputStream(_keyChain)){
                _ks.store(os,_password.toCharArray());
            }
        } catch (Exception e) {
            Log.e("CREATE_KEY", e.toString());
        }
    }
    public void cipher(String data) {
        try {
            Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
            aes.init(Cipher.ENCRYPT_MODE, _key);

            FileOutputStream outputStream = new FileOutputStream(_data);
            CipherOutputStream out = new CipherOutputStream(outputStream, aes);
            out.write(data.getBytes());
            out.flush();
            out.close();
        }catch (Exception e) {
            Log.e("CIPHER", e.toString());
        }
    }

    private boolean decipher(int file_size) {
        if (this.getKey()) {
            try {
                Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
                aes.init(Cipher.DECRYPT_MODE, _key);

                FileInputStream fis = new FileInputStream(_data);
                CipherInputStream in = new CipherInputStream(fis, aes);

                byte[] b = new byte[file_size];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                int numberOfByteRead;
                while ((numberOfByteRead = in.read(b)) >= 0) {
                    baos.write(b, 0, numberOfByteRead);
                }
                if ( baos.size() > 0) {
                    _content = new String(baos.toByteArray());
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                Log.e("DPHER", e.toString());
                return false;
            }
        } else
            return false;
    }

    private void getJsonContent(int file_size) {
        try {
            _jo = new JSONObject(this.getContent(file_size));
        } catch (Exception e) {
            Log.e("JSONContent", e.toString());
        }

    }

    private void getCounter(int file_size) {
        try {
            this.getJsonContent(file_size);
            _counter = Integer.parseInt(_jo.get("trial_counter").toString());
        } catch (Exception e) {
            Log.e("getCounter", e.toString());
        }
    }

    private void save (String key, boolean value) {
        try {
            _jo.put(key,value);
            this.cipher(_jo.toString());

        } catch (Exception e) {
            Log.e("SAVE", e.toString());
        }
    }
    private void save (String key, long value) {
        try {
            _jo.put(key,value);
            this.cipher(_jo.toString());

        } catch (Exception e) {
            Log.e("SAVE", e.toString());
        }
    }
    private void save (String key, double value) {
        try {
            _jo.put(key,value);
            this.cipher(_jo.toString());

        } catch (Exception e) {
            Log.e("SAVE", e.toString());
        }
    }
    private void save (String key, int value) {
        try {
            _jo.put(key,value);
            this.cipher(_jo.toString());

        } catch (Exception e) {
            Log.e("SAVE", e.toString());
        }
    }
    private void save (String key, Object value) {
        try {
            _jo.put(key,value);
            this.cipher(_jo.toString());

        } catch (Exception e) {
            Log.e("SAVE", e.toString());
        }
    }

    private long getTimer (int file_size) {
        this.getJsonContent(file_size);
        try {
            return _jo.getLong("count_down");
        }catch (Exception e) {
            return Long.MAX_VALUE;
        }

    }

    private void setTimer( long utc, int file_size) {
        this.getJsonContent(file_size);
        this.save("count_down",utc);
    }

    private void setCurrentTime ( int file_size) {
        this.getJsonContent(file_size);
        this.save("lock_time", System.currentTimeMillis());
    }

    public void tripTimer(int file_size) {
        long _days = 3 * 24 * 60 * 60 * 1000;
        this.setCurrentTime(file_size);
        this.setTimer(_days + System.currentTimeMillis(),file_size);
    }

    public String getRemainingTime(int file_size) {
        long _storedTime = this.getTimer(file_size);
        long _minutes = (_storedTime - System.currentTimeMillis() )/(1000*60);
        long _hours = _minutes /60;
        long _days = _hours /24;

        if (_storedTime != Long.MAX_VALUE)
             return " " + Long.toString(_days) + " days " + Long.toString(_hours % 24) + " hours " + Long.toString(_minutes % 60) + " minutes ";
        else
            return " Unknown duration ";

    }


    public boolean isLockedOut(int file_size) {
        try {
            this.getJsonContent(file_size);
            return  (_jo.getBoolean("isLocked"));
        } catch (Exception e){
            Log.e("LOCK", e.toString());
            return true;
        }
    }
    public void setLock(boolean state, int file_size) {
        this.getJsonContent(file_size);
        this.save("isLocked",state);
    }

    public int decrementCounter (int file_size) {
        this.getJsonContent(file_size);
        this.getCounter(file_size);
        if ( _counter != 0) {
            _counter = _counter - 1;
        } else {
            this.save("isLocked",true);
        }
        this.save("trial_counter",_counter);
        return _counter;
    }

    public void resetCounter (int file_size) {
        _counter = 3;
        this.getJsonContent(file_size);
        this.save("trial_counter",_counter);

    }

    public void setCounter(int count, int file_size) {

        this.getJsonContent(file_size);
        this.save("trial_counter", count);
    }
    public void addCode(String code, int file_size) {
        this.getJsonContent(file_size);
        JSONArray _ja = new JSONArray();
        _ja.put(code);
        this.save("codes",_ja);
    }

    public boolean isCodeValid(String codeEntered, int file_size) {
        this.getJsonContent(file_size);
        ArrayList<String> codeArray = new ArrayList<>();
        try {
            JSONArray _ja = _jo.getJSONArray("codes");
            if (_ja != null) {
                for (int i = 0; i < _ja.length(); i++) {
                    codeArray.add(_ja.getString(i));
                }
            }
            if (codeArray.contains(codeEntered)) return true;
            else return false;
        }catch (Exception e){
            Log.e("CodeValidate", e.toString());
            return false;
        }
    }

    public String getContent(int file_size) {
        if (this.decipher(file_size)){
            return _content;
        } else {
            return "{}";
        }
    }




}

/*
* HOW TO USE:
* BirdsLightUtils bl = new BirdsLightUtils(this, "jamesianfauni");
* String myContent = bl.getContent(1024);
* Toast.makeText(MainActivity.this, myContent, Toast.LENGTH_LONG).show();
* */
