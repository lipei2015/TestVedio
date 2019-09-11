// IFaceApi.aidl
package com.wrtsz.intercom.master;

// Declare any non-default types here with import statements

interface IFaceApi {
    /**
     * 接口功能：
     * 检测给定图片中的人脸（Face） 的位置和人脸质量信息， 位置由一个四元素
       坐标表示 (x， y， w， h)， 人脸质量为 100 以内的数值

     * 输入参数:
     * image 否 String 图片 base64 数据。 支持 PNG、 JPG、 JPEG、 BMP， 不
       支持 GIF 图片。
       url 否 String 图片的 url、 image 必须提供一个， 如果都提供， 只
       使用 url。
       max_face_num
       否 Integer 最多处理的人脸数目。 默认值为 1（仅检测图片中面
       积最大的那张人脸） ， 最大值为 5。 此参数用于控
       制处理待检测图片中的人脸个数， 值越小， 处理速度
       越快。
       min_face_size
       否 Integer 人脸长和宽的最小尺寸， 单位为像素。 默认为 40。 低
       于此尺寸的人脸不会被检测。
       need_quality_detection
       否 Integer 是否开启质量检测。0 为关闭，1 为开启。默认为 0。
       非 1 值均视为不进行质量检测。
       建议： 人脸入库操作建议开启此功能

       输出参数:

       RetStr
       String
       接口参数调用返回状态描述。
       “ok” :表示调用成功：
       “failed_url_error” :使用 url 方式时， 可能返回该错误， 表
       示对应的 url 非法、 下载后无法解码等。
       “failed_image_error”:使用 base64 方式时,可能返回该错误，
       表示对应的 base64 无法解析成图片或者解析成不支持的图片。

       DeviceFaceInfos
        JSON
        人脸信息列表， 是一个 json 数组， 只有当 RetStr 为“ok” 时，
       该参数才有实际意义。 如果没有输入参数
       NeedQualityDetection， 则 DeviceFaceInfos 中就不包含
       FaceQualityScore。 如果没有人脸， 则该项内容为空

       示例：
       {
       "RetStr":"ok",
       "DeviceFaceInfos": [
       {
       "X": 156,
       "Y": 129,
       "Width": 196,
       "Height": 196,
       "FaceQualityScore": 95
       }
       ]
       }
     *
     */
    String detect(String image,
        String url,
        int max_face_num,
        int min_face_size,
        int need_quality_detection
        );

    /*
    *
    * 接口功能： 将输入的图片注册到人脸库中
    * 输入参数：
    * 参数名称 必选 类型 描述
      image 否 String 图片 base64 数据。 支持 PNG、 JPG、 JPEG、 BMP， 不支
      持 GIF 图片。
      url 否 String 图片的 Url、 Image 必须提供一个， 如果都提供， 只使
      用 Url。
      person_name 是 String 人员名称， [1， 60]个字符。
      black_list 否 Integer 1： 表示注册成黑名单； 0： 表示正常注册； 不传默认为
      0。

      输出参数
      参数名称 类型 描述
      RetStr String
      接口参数调用返回状态描述。
      “ok” :表示注册成功：
      “failed_url_error” :使用 url 方式时， 可能返回该错误， 表示对
      应的 url 非法、 下载后无法解码等。
      “failed_image_error” :使用 base64 方式时,可能返回该错误， 表
      示对应的 base64 无法解析成图片或者解析成不支持的图片。
      “failed_face/reg_error” ： 表示所输入的图片无法正常注册， 可
      能的原因： （1） 图片有超过 1 个以上的人脸； （2） 图片人脸质量
      太差， 无法正常注册。 （3） 提供的图片之前已注册过。
      PersonId String 人脸唯一标识。 如果注册失败， 则该项内容为空。

      示例
      {
      "RetStr":"ok",
      "PersonId":"2877242150180891493"
      }
    */
    String reg(String image,
           String url,
           String person_name,
           int black_list
           );


    /*
    *
    * 接口功能： 从人脸库中注销由 person_id 指定的人脸。
    * 输入参数
      参数名称 必选 类型 描述
      person_id 是 String 人脸唯一标识。 由人脸注册接口 face/reg 返回的。

      输出参数
      RetStr String 接口参数调用返回状态描述。
      “ok” :表示注销成功：
      “failed_invalid_person_id” :表示人脸库中没有该 person_id。
    */
    String unreg(String person_id);


    /*
    *接口功能： 编辑由 PersonId 指定的人脸信息， 比如是否为黑名单、 修改 person_name
     等
     输入参数
     参数名称 必选 类型 描述
     person_id 是 String 人脸唯一标识。 由人脸注册接口 face/reg 返回的。
     person_name 否 String 该参数可以缺失， 如果缺失， 表示不修改 PersonName
     属性。
     black_list 否 Integer 该参数可以缺失， 如果缺失， 表示不修改 BlackList
     属性

     输出参数
     参数名称 类型 描述
     RetStr String 接口参数调用返回状态描述。
     “ok” :表示编辑成功：
     “failed_invalid_personid” :表示人脸库中没有该 PersonId。
    */
    String edit(String person_id,
        String person_name,
        int black_list);

    /*
    *接口功能： 根据输入的图片， 识别是否为人脸库中的某人。 如果识别成功， 则返回对应
     的 PersonId 和人脸位置四元组。

     输入参数
     参数名称 必选 类型 描述
     image 否 String 图片 base64 数据。 支持 PNG、 JPG、 JPEG、 BMP， 不支持 GIF
     图片。
     url 否 String 图片的 Url、Image 必须提供一个，如果都提供，只使用 Url。

     输出参数
     参数名称 类型 描述
     RetStr Strin
     g
     接口参数调用返回状态描述。
     “ok” :表示调用成功：
     “failed_url_error” :使用 url 方式时， 可能返回该错误， 表示对
     应的 url 非法、 下载后无法解码等。
     “failed_image_error” :使用 base64 方式时,可能返回该错误， 表
     示对应的 base64 无法解析成图片或者解析成不支持的图片。
     FaceInfo
     s
     JSON 人脸信息列表， 是一个 json 数组。 如果识别失败（不在人脸库或者
     提供的图片不包含人脸， 或者打开活体检测但是提供的图片不是真
     人） ， 则该项为空。
    */
    String recognition(String image,
                                  String url);

     /*
     *接口功能：获取当前人脸识别使用的阈值与活体检测开关。
     * 输入参数：无
     * 输出参数
       参数名称 类型 描述
       Threshold Integer 人脸识别的阈值，范围为 0-100 的整数
       LiveDetect Integer 是否打开活体，只有 1 和 0 两个值。

       示例
       {
       "Threshold":85,
       "LiveDetect":1
       }
     */
     String recognition_config_get();

     /**
     *接口功能：设置人脸识别阈值和活体检侧开关，需要在调用人脸识别之前调用该接口。
      如果没有设置，则使用系统默认的配置
      输入参数
      参数名称 必选 类型 描述
      Threshold 否 Integer 设置人脸检测的阈值，范围为 0-100 的整数。阈值设太
      大容易导致无法识别，设太小容易导致误识别。
      该参数可以缺失,如果缺失，表示不修改阈值。
      LiveDetect 否 Integer 只能取值为 0 或者 1。
      0 表示关闭活体检测功能；
      1 表示打开活体检测功能

      输出参数
      参数名称 类型 描述
      RetStr String 接口参数调用返回状态描述。
      “ok” :表示设置成功：
      “failed_invalid_threshold” :表示传入一个非法的 Threshold，
      比如传入的不是整数，或者是一个超出(0,100)的数字。
      “failed_invalid_livedetect”:表示传入一个非法的 livedetect.
     */
     String recognition_config(int Threshold,
                                    int LiveDetect);

}
