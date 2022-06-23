package com.aftab.abentseller.Utils

object Constants {


    const val OTHERS = "Others"
    const val FEMAlE = "Female"
    const val MAlE = "Male"
    const val ONLINE_STATUS = "onlineStatus"
    const val OFFLINE = "offline"
    const val ONLINE = "online"
    const val DELIVERY_BOY_UID="deliveryBoyUid"
    const val ASSIGNED="assigned"
    const val BLOCKED = "Blocked"
    const val READ = "read"
    const val RECEIVER_ID = "receiverId"
    const val NOTIFICATIONS = "notifications"
    const val ORDER_NOTI = "orderNoti"
    const val REVIEW_NOTI = "reviewNoti"
    const val CHAT_NOTI = "chatNoti"
    const val DELIVERY_NOTI = "deliveryNoti"


    const val FCM = "fcm"
    const val FILEPATH = "storage/emulated/0/AbentSeller/"
    const val DIRECTORY_NAME = "AbentUser"

    const val SEND_VIDEO_URL = "send_video_url"
    const val SEND_IMAGE = "send_img"

    const val LAYOUT_SEND_MSG = 1
    const val LAYOUT_RECEIVE_MSG = 2
    const val LAYOUT_SEND_IMG = 3
    const val LAYOUT_RECEIVE_IMG = 4
    const val LAYOUT_SEND_LOCATION = 5
    const val LAYOUT_RECEIVE_LOCATION = 6
    const val LAYOUT_SEND_FILE = 7
    const val LAYOUT_RECEIVE_FILE = 8
    const val LAYOUT_SENDER_VIDEO = 9
    const val LAYOUT_RECEIVE_VIDEO = 10
    const val LAYOUT_TIME = 11
    const val LAYOUT_SEND_RELATED_TO = 12
    const val LAYOUT_RECEIVE_RELATED_TO = 13

    const val PICK_FILE_REQUEST_CODE = 1
    const val REQUEST_TAKE_GALLERY_VIDEO = 2
    const val OPEN_MAP_ACTIVITY = 3

    const val TEXT = "txt"
    const val PNG = "png"
    const val JPG = "jpg"
    const val MP4 = "mp4"
    const val PDF = "pdf"
    const val DOCX = "docx"
    const val APK = "apk"
    const val ZIP = "zip"
    const val JAVA = "java"
    const val XML = "xml"

    const val KEY_REF_CHATS = "chat"

    const val SELLER_UID = "sellerUid"
    const val ORDER = "order"
    const val CREATE_ACC = "createAccount"
    const val ID = "id"
    const val ORDER_STATUS = "orderStatus"
    const val ENGLISH = "english"
    const val CHINESE = "chinese"
    const val ADDRESS_LNG = "addressLng"
    const val ADDRESS_LAT = "addressLat"
    const val PRODUCTS = "products"
    const val PRODUCT_CATEGORIES = "productCategories"
    const val ABOUT = "about"
    const val DTB = "dtb"
    const val GENDER = "gender"
    const val COUNTRY = "country"
    const val CITY = "city"
    const val DP = "dp"
    const val PLAN_TYPE = "planType"
    const val PLAN_DATE = "planDate"
    const val USER_TYPE = "userType"
    const val LOGIN_TYPE = "loginType"
    const val USER_STATUS = "userStatus"
    const val ACTIVE = "Active"
    const val FREE = "Free"
    const val PHONE = "phone"
    const val UID = "uid"
    const val FROM = "from"
    const val LOCATION = "location"
    const val LAT = "lat"
    const val LNG = "lng"
    const val CODE = "code"
    const val SELLER = "Seller"
    const val SELLERS = "Sellers"
    const val USERS = "USERS"
    const val IS_ADDED_INFO = "isInfoAdded"
    const val IS_EMAIL_SENT = "isEmailSent"
    const val KEY_PREFERENCE_NAME: String = "AbentSeller"
    const val SPLASH_DURATION: Long = 1000
    const val IS_LOGGED_IN = "isLodgedIn"
    const val FIRST_NAME = "fName"
    const val LAST_NAME = "lName"
    const val EMAIL = "email"
    const val PASSWORD = "password"
    private const val ACCESS_KEY = "sk286292djd926d"
    private const val BASE_URL = "https://Apis.appistaan.com/mailapi/"
    const val MAIL_URL = BASE_URL + "index.php?key=" + ACCESS_KEY
    const val SEND_ID = "sendId"

    // For Noti
    const val REMOTE_MSG_TYPE = "type"
    const val KEY_TITLE = "title"
    const val KEY_BODY = "body"
    const val REMOTE_MSG_INVITER_TOKEN = "inviterToken"
    const val REMOTE_MSG_DATA = "data"
    const val REMOTE_MSG_REGISTRATION_IDS = "registration_ids"
    private const val REMOTE_MSG_AUTHORIZATION = "Authorization"
    private const val REMOTE_MSG_CONTENT_TYPE = "Content-Type"
    const val REMOTE_MSG_CHAT = "remote_msg_chat"
    const val REMOTE_MSG_ORDER = "remote_msg_order"

    fun getRemoteMessageHeaders(): HashMap<String, String>? {
        val headers = HashMap<String, String>()
        headers[REMOTE_MSG_AUTHORIZATION] =
            "key=AAAAwmDzLmE:APA91bGcnVJTGyTnipWALlCdB_JiJqvPsSjPe8ozXbsFhn5UlJ6MAn2IqP9ikMJK2YW2zxmsNuZPkkGAN73gTrlUoyZziZT1aywflU_Yl9Q4oS76qdXi3CucvPyyHPHF3NKzID6CUyd1"
        headers[REMOTE_MSG_CONTENT_TYPE] = "application/json"
        return headers
    }
}