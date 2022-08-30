package com.codezon.ludofantacy.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppModel {

    @SerializedName("result")
    private List<Result> result;

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public static class Result {

        @SerializedName("msg")
        private String msg;

        @SerializedName("success")
        private int success;

        @SerializedName("country_code")
        private String country_code;

        @SerializedName("currency_code")
        private String currency_code;

        @SerializedName("currency_sign")
        private String currency_sign;

        @SerializedName("paytm_mer_id")
        private String paytm_mer_id;

        @SerializedName("paytm_mer_key")
        private String paytm_mer_key;

        @SerializedName("refer_percentage")
        private int refer_percentage;

        @SerializedName("maintenance_mode")
        private int maintenance_mode;

        @SerializedName("fcm_server_key")
        private String fcm_server_key;

        @SerializedName("payu_id")
        private String payu_id;

        @SerializedName("payu_key")
        private String payu_key;

        @SerializedName("payu_salt")
        private String payu_salt;

        @SerializedName("mop")
        private int mop;

        @SerializedName("cus_support_email")
        private String cus_support_email;

        @SerializedName("how_to_play")
        private String how_to_play;

        @SerializedName("force_update")
        private String force_update;

        @SerializedName("whats_new")
        private String whats_new;

        @SerializedName("update_date")
        private String update_date;

        @SerializedName("latest_version_name")
        private String latest_version_name;

        @SerializedName("latest_version_code")
        private String latest_version_code;

        @SerializedName("update_url")
        private String update_url;


        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public int getSuccess() {
            return success;
        }

        public void setSuccess(int success) {
            this.success = success;
        }

        public String getCurrency_code() {
            return currency_code;
        }

        public void setCurrency_code(String currency_code) {
            this.currency_code = currency_code;
        }

        public String getCurrency_sign() {
            return currency_sign;
        }

        public void setCurrency_sign(String currency_sign) {
            this.currency_sign = currency_sign;
        }

        public String getPaytm_mer_id() {
            return paytm_mer_id;
        }

        public void setPaytm_mer_id(String paytm_mer_id) {
            this.paytm_mer_id = paytm_mer_id;
        }

        public String getPaytm_mer_key() {
            return paytm_mer_key;
        }

        public void setPaytm_mer_key(String paytm_mer_key) {
            this.paytm_mer_key = paytm_mer_key;
        }

        public int getRefer_percentage() {
            return refer_percentage;
        }

        public void setRefer_percentage(int refer_percentage) {
            this.refer_percentage = refer_percentage;
        }

        public int getMaintenance_mode() {
            return maintenance_mode;
        }

        public void setMaintenance_mode(int maintenance_mode) {
            this.maintenance_mode = maintenance_mode;
        }

        public String getFcm_server_key() {
            return fcm_server_key;
        }

        public void setFcm_server_key(String fcm_server_key) {
            this.fcm_server_key = fcm_server_key;
        }

        public String getPayu_id() {
            return payu_id;
        }

        public void setPayu_id(String payu_id) {
            this.payu_id = payu_id;
        }

        public String getPayu_key() {
            return payu_key;
        }

        public void setPayu_key(String payu_key) {
            this.payu_key = payu_key;
        }

        public String getPayu_salt() {
            return payu_salt;
        }

        public void setPayu_salt(String payu_salt) {
            this.payu_salt = payu_salt;
        }

        public int getMop() {
            return mop;
        }

        public void setMop(int mop) {
            this.mop = mop;
        }

        public String getCus_support_email() {
            return cus_support_email;
        }

        public void setCus_support_email(String cus_support_email) {
            this.cus_support_email = cus_support_email;
        }

        public String getHow_to_play() {
            return how_to_play;
        }

        public void setHow_to_play(String how_to_play) {
            this.how_to_play = how_to_play;
        }

        public String getForce_update() {
            return force_update;
        }

        public void setForce_update(String force_update) {
            this.force_update = force_update;
        }

        public String getWhats_new() {
            return whats_new;
        }

        public void setWhats_new(String whats_new) {
            this.whats_new = whats_new;
        }

        public String getUpdate_date() {
            return update_date;
        }

        public void setUpdate_date(String update_date) {
            this.update_date = update_date;
        }

        public String getLatest_version_name() {
            return latest_version_name;
        }

        public void setLatest_version_name(String latest_version_name) {
            this.latest_version_name = latest_version_name;
        }

        public String getLatest_version_code() {
            return latest_version_code;
        }

        public void setLatest_version_code(String latest_version_code) {
            this.latest_version_code = latest_version_code;
        }

        public String getUpdate_url() {
            return update_url;
        }

        public void setUpdate_url(String update_url) {
            this.update_url = update_url;
        }
    }
}
