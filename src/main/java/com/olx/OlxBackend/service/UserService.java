package com.olx.OlxBackend.service;

import com.olx.OlxBackend.DTO.UserDto;
import com.olx.OlxBackend.model.ApplicationUser;
import com.olx.OlxBackend.repository.UserRepository;
import com.olx.OlxBackend.transformer.UserTransformer;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.http.message.BasicNameValuePair;
import org.aspectj.apache.bcel.classfile.annotation.NameValuePair;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;

@Service
public class UserService {
    @Value("${twilio.account_sid}")
    private String ACCOUNT_SID;

    @Value("${twilio.auth_token}")
    private String AUTH_TOKEN;

    @Value("${twilio.phone_number}")
    private String TWILIO_PHONE_NUMBER;
    @Autowired
    UserRepository userRepository;
    public ApplicationUser signUp(UserDto userDto) {
        ApplicationUser userByEmailId = userRepository.findByEmailId(userDto.getEmailId());
        if (userByEmailId != null) throw new RuntimeException("Email id already register");
        ApplicationUser userMobileNumber = userRepository.findByPhoneNumber(userDto.getPhoneNumber());
        if (userMobileNumber != null) throw new RuntimeException("Mobile Number is already used");
//        mobileVerification(userDto.getPhoneNumber(), body);
        ApplicationUser savedApplicationUser = userRepository.save(UserTransformer.UserDtoToUserObject(userDto));
        return savedApplicationUser;
    }
    public Object signIn(String userName, String password) {
        ApplicationUser user = null;
        if (userName.contains("@")) user = userRepository.findByEmailId(userName);
        else user = userRepository.findByPhoneNumber(userName);

        if (user == null) throw new RuntimeException("User Name is Incorrect");
        if (!user.getPassword().equals(password)) throw new RuntimeException("Password is Incorrect");
        try {
            String otp = generateOtp();
            boolean otpSendSuccessful = userName.contains("@") ? sendMail(userName, otp) : mobileVerification(userName);
            user.setOtp(otp);
            return "Otp Send To Your : " + userName;
        } catch (Exception exception) {
            return exception.getMessage();
        }
    }

    private boolean sendMail(String userName, String otp) {

        String body = getBodyForSms(otp);
        String senderEmail = "aniruddhamukherjee232@gmail.com";
        String appPassword = "sszv fvjp hzsy iaby";
        String recipientEmail = userName;

        // Email configuration
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Authenticate sender
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, appPassword);
            }
        });

        try {
            // Create a message
            javax.mail.Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Application for Java Backend Developer Position");

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Now set the actual message
            messageBodyPart.setText(body);

            // Create a multipart message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);

            // Send message
            Transport.send(message);
            System.out.println(userName);
            return true;
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getBodyForSms(String otp) {
        return "Your OTP is: " + otp + ". Please use this OTP to complete your verification.";
    }

    private String generateOtp() {
        int length = 4;
        // Possible characters in the OTP
        String numbers = "0123456789";
        // Random object
        Random random = new Random();
        // StringBuilder to store generated OTP
        StringBuilder sb = new StringBuilder();

        // Generate OTP of specified length
        for (int i = 0; i < length; i++) {
            // Generate a random index between 0 and length of numbers string
            int index = random.nextInt(numbers.length());
            // Append the character at the randomly generated index to the OTP
            sb.append(numbers.charAt(index));
        }

        return sb.toString();
    }

    public boolean mobileVerification(String to) {
        String otp = generateOtp();
        String body = getBodyForSms(otp);
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message.creator(
                        new PhoneNumber(to),
                        new PhoneNumber(TWILIO_PHONE_NUMBER),
                        body)
                .create();

        System.out.println("Message SID: " + message.getSid());
        return true;
    }

    public void userVerify(String otp, ApplicationUser applicationUser, HashMap<String, Object> result) {
        if(applicationUser.getOtp().equals(otp)) result.put("success", "User Id is Verified");
        else result.put("error", "Entered Wrong Otp");
    }
}
