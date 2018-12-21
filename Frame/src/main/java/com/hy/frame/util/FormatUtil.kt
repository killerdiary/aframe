package com.hy.frame.util

import java.util.regex.Pattern

/**
 * 常用的格式验证
 * @author HeYan
 * @time 2018/4/6 10:22
 */
object FormatUtil {
    /**
     * 是否包含特殊符号
     */
    fun isContainSpecialSymbols(str: String?): Boolean {
        if (str == null) return false
        return !Pattern.compile("[。，、：；？！‘’“”′.,﹑:;?!'\"〝〞＂+\\-*=<_~#\$&%‐﹡﹦﹤＿￣～﹟﹩﹠﹪@﹋﹉﹊｜‖^·¡…︴﹫﹏﹍﹎﹨ˇ¨¿—/（）〈〉‹›﹛﹜『』〖〗［］《》〔〕{}」【】︵︷︿︹﹁﹃︻︶︸﹀︺︾ˉ﹂﹄︼]").matcher(str).matches()
    }

    /**
     * 是否是图片地址
     */
    fun isImagePath(str: String?): Boolean {
        if (str == null) return false
        return str.matches("(?i).+?\\.(png|jpg|gif|bmp)".toRegex())
    }

    /**
     * 手机号验证 模糊验证
     */
    fun isMobileNumber(str: String?): Boolean {
        if (str == null) return false
        return str.matches("^[1][3-9][0-9]{9}$".toRegex())
    }

    /**
     * 手机号验证 精确验证 慎用
     */
    fun isExactMobileNumber(str: String?): Boolean {
        if (str == null) return false
        return str.matches("^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$".toRegex())
    }

    /**
     * 电话号码验证(手机号码和座机号)
     */
    fun isPhoneNumber(str: String?): Boolean {
        var text: String = str ?: return false
        var b = false
        text = text.replace("-".toRegex(), "")
        if (text.length == 11) {
            val pattern = Pattern.compile("^[0][1-9]{2,3}[0-9]{5,10}$") // 验证带区号的
            val m = pattern!!.matcher(str)
            b = m!!.matches()
        } else if (str.length <= 9) {
            val pattern = Pattern.compile("^[1-9][0-9]{5,8}$") // 验证没有区号的
            val m = pattern!!.matcher(str)
            b = m!!.matches()
        }
        if (!b)
            return isMobileNumber(str)
        return b
    }

    /**
     * 是否是数字
     */
    fun isNumber(str: String?): Boolean {
        if (str == null) return false
        return Pattern.compile("[0-9]+").matcher(str).matches()
    }

    /**
     * 是否是英文
     */
    fun isEnglish(str: String?): Boolean {
        if (str == null) return false
        return Pattern.compile("[a-zA-Z]+").matcher(str).matches()
    }

    /**
     * 是否是中文
     */
    fun isChinese(str: String?): Boolean {
        if (str == null) return false
        return Pattern.compile("^[\u4e00-\u9fa5]+$").matcher(str).matches()
    }

    /**
     * 是否是IP地址
     */
    fun isIpAddress(str: String?): Boolean {
        if (str == null) return false
        return Pattern.compile("(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}").matcher(str).matches()
    }

    /**
     * 是否是身份证
     */
    fun isIdentityNumber(str: String?): Boolean {
        if (str == null) return false
        if (str.length == 15)
            return Pattern.compile("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$").matcher(str).matches()
        if (str.length == 18) {
            var text = str
            if (str.contains("x"))
                text = str.replace("x".toRegex(), "X")
            return Pattern.compile("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$").matcher(text).matches()
        }
        return false
    }

    /**
     * 邮箱验证
     * @param str
     * @return 验证通过返回true
     */
    fun isEmail(str: String?): Boolean {
        if (str == null) return false
        return Pattern
                .compile("^([a-zA-Z0-9_\\-.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(]?)$")
                .matcher(str).matches()
    }

    /**
     * 是否是银行卡号
     */
    fun isBankCard(str: String?): Boolean {
        if (str == null) return false
        return Pattern.compile("^(\\d{16}|\\d{19})$").matcher(str).matches()
    }

    /**
     * 去掉数中多余的0
     */
    fun removeNumberUselessZero(str: String): String {
        if (isEmpty(str)) return "0"
        var text = str
        if (text.indexOf(".") > 0) {
            text = text.replace("0+?$".toRegex(), "")// 去掉多余的0
            text = text.replace("[.]$".toRegex(), "")// 如最后一位是.则去掉
        }
        return text
    }

    /**
     * 转换成Money格式
     */
    fun formatToMoney(obj: Any?): String {
        val pattern = "0.00"
        if (obj == null) return pattern
        return java.text.DecimalFormat(pattern).format(obj)
    }

    /**
     * 是否是车牌号
     */
    fun isCarNumber(str: String): Boolean {
        if (isNoEmpty(str))
            return Pattern.compile("^[\u4e00-\u9fa5|A-Z][A-Z][A-Z_0-9]{5}$").matcher(str).matches()
        return false
    }



    /**
     * 是否不为空
     */
    fun isNoEmpty(str: String?): Boolean {
        return !isEmpty(str)
    }

    /**
     * 是否为空 排除null
     */
    fun isEmpty(str: String?): Boolean {
        if (str.isNullOrBlank()) return true
        if (str!!.equals("null", true)) return true
        return false
    }

    /**
     * 是否不为空
     */
    fun isNoEmpty(datas: MutableList<*>?): Boolean {
        return !isEmpty(datas)
    }

    /**
     * 是否为空
     */
    fun isEmpty(datas: MutableList<*>?): Boolean {
        if (datas == null) return true
        if (datas.isEmpty()) return true
        return false
    }
}