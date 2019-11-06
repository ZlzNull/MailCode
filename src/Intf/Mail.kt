package com.zlz.Intf

import com.zlz.Dao.saveCode
import java.util.*
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

fun sendMail(receiveMail: String, type: Boolean) {
    val sendMail = "1163452838@qq.com"
    val password = "fifzgjpmbwmnbaai"
    val props = Properties()
    props.setProperty("mail.smtp.ssl.enable", "true")
    //需要请求认证
    props.setProperty("mail.smtp.auth", "true")
    //使用的协议（JavaMail规范要求）
    props.setProperty("mail.transport.protocol", "smtp")
    //发件人的邮箱的 SMTP 服务器地址
    props.setProperty("mail.smtp.host", "smtp.qq.com")

    props.setProperty("mail.smtp.port", "465")
    props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
    props.setProperty("mail.smtp.socketFactory.fallback", "false")
    props.setProperty("mail.smtp.socketFactory.port", "465")

    val session = Session.getInstance(props)

//    session.debug = true

    val message = createMimeMessage(session, sendMail, receiveMail, type)
    val transport = session.transport
    transport.connect(sendMail, password)
    transport.sendMessage(message, message.allRecipients)
    transport.close()
}

fun createMimeMessage(session: Session, sendMail: String, receiveMail: String, type: Boolean): MimeMessage {
    val message = MimeMessage(session)
    message.setFrom(InternetAddress(sendMail, "西邮派", "UTF-8"))
    message.addRecipient(Message.RecipientType.TO, InternetAddress("$receiveMail@qq.com", "我的测试邮件_收件人昵称", "UTF-8"))
    message.setSubject("西邮派邮箱验证邮件", "UTF-8")

    val text = MimeBodyPart()
    val a =
        """<div id="contentDiv" onmouseover="getTop().stopPropagation(event);" onclick="getTop().preSwapLink(event, 'spam', 'ZC2222-dZH7YqmoSEJPWUsWzmgne96');" style="position:relative;font-size:14px;height:auto;padding:15px 15px 10px 15px;z-index:1;zoom:1;line-height:1.7;" class="body">    <div id="qm_con_body"><div id="mailContentContainer" class="qmbox qm_con_body_content qqmail_webmail_only">
                <table width="700" border="0" align="center" cellspacing="0" style="width:700px;">
                <tbody>
                    <tr>
                        <td>
                            <div style="width:700px;margin:0 auto;border-bottom:1px solid #ccc;margin-bottom:30px;">
                                <table border="0" cellpadding="0" cellspacing="0" width="700" height="39" style="font:12px Tahoma, Arial, 宋体;">
                                    <tbody>
                                        <tr>
                                            <td width="210"></td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                            <div style="width:680px;padding:0 10px;margin:0 auto;">
                                <div style="line-height:1.5;font-size:14px;margin-bottom:25px;color:#4d4d4d;">
                                    <strong style="display:block;margin-bottom:15px;">
                                        亲爱的会员：
                                        <span style="color:#f60;font-size: 16px;"></span>您好！
                                    </strong>
                                    <strong style="display:block;margin-bottom:15px;">
                                        您正在
    """.trimIndent()
    val b by lazy {
        if (type) {
            "注册账号"
        } else {
            "修改密码"
        }
    }
    val c = """，请在验证码输入框中输入： <span style="color:#f60;font-size: 24px">""".trimIndent()
    val d by lazy {
        var a = ""
        for (i in 1..6) {
            a += (0..9).random()
        }
        a
    }
    val e = """</span>，以完成操作。</strong></div>
                <div style="margin-bottom:30px;">
                    <small style="display:block;margin-bottom:20px;font-size:12px;">
                        <p style="color:#747474;">
                            注意：此操作可能会修改您的密码。如非本人操作，请及时登录并修改密码以保证帐户安全
                            <br>（工作人员不会向你索取此验证码，请勿泄漏！)
                        </p>
                    </small>
                </div>
            </div>
            <div style="width:700px;margin:0 auto;">
                <div style="padding:10px 10px 0;border-top:1px solid #ccc;color:#747474;margin-bottom:20px;line-height:1.3em;font-size:12px;">
                    <p>此为系统邮件，请勿回复<br>
                        请保管好您的邮箱，避免账号被他人盗用
                    </p>
                    <p>西邮派版权所有<span style="border-bottom:1px dashed #ccc;z-index:1" t="7" onclick="return false;" data="1999-2014">2019-2019</span></p>
                </div>
            </div>
        </td>
    </tr>
    </tbody>
</table>
<style type="text/css">.qmbox style, .qmbox script, .qmbox head, .qmbox link, .qmbox meta {display: none !important;}</style></div></div><style>#mailContentContainer .txt {height:auto;}</style>  </div>
    """.trimIndent()
    text.setContent(a + b + c + d + e, "text/html;charset=UTF-8")
    val mm = MimeMultipart()
    mm.addBodyPart(text)
    mm.setSubType("mixed")
    message.setContent(mm)
    message.sentDate = Date()
    message.saveChanges()

    saveCode(receiveMail, d)

    return message
}