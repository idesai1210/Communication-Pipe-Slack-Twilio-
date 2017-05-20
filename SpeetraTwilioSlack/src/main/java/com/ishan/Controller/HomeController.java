package com.ishan.Controller;

import com.github.seratch.jslack.Slack;
import com.github.seratch.jslack.api.methods.request.channels.ChannelsListRequest;
import com.github.seratch.jslack.api.methods.request.chat.ChatPostMessageRequest;
import com.github.seratch.jslack.api.methods.response.channels.ChannelsListResponse;
import com.github.seratch.jslack.api.methods.response.chat.ChatPostMessageResponse;
import com.github.seratch.jslack.api.model.Channel;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;
import com.twilio.sdk.resource.list.MessageList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ishan on 5/19/17.
 */
@RestController
public class HomeController {

    @Autowired
    public static final String ACCOUNT_SID = "AC274c600e620c77279b970cd1394248b9";
    public static final String AUTH_TOKEN = "ded2baf31a81dd4d80ef97e07051b54f";
    public static final String TWILIO_NUMBER = "+14697897673";
    public static final String USER_NUMBER = "+14694380988";
    public static final String SLACK_WEBHOOK_SECRET = "HTWkwStbpYWKq1dh3JUZiQk2";




    @RequestMapping("/greeting")
    public String greeting() {
        return "greeting.html";
    }


    //From User to Slack
    @RequestMapping(value = "/twilio",method = RequestMethod.POST)
    public void sendSMS() {
        try {


            //Get Message info from Twilio Client
            TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

            MessageList ml = client.getAccount().getMessages();
            String body = "";
            String number = "";
            for(Message m : ml){
                body = m.getBody();
                number = m.getFrom();
                break;
                //System.out.print(m.getBody());

            }
            System.out.println(body);


            //Send message to Slack
            //Check if the user is authorized or not
            if(number.equals(USER_NUMBER)) {

                //Slack Token
                String token = "xoxp-185395272242-185322117107-184728080768-9351644e95383154a3d0dafdc9ed91f8";
                Slack slack = Slack.getInstance();

                // find all channels in the team
                ChannelsListResponse channelsResponse = slack.methods().channelsList(
                        ChannelsListRequest.builder().token(token).build());

                // find #general
                Channel general = channelsResponse.getChannels().stream()
                        .filter(c -> c.getName().equals("general")).findFirst().get();

                // https://slack.com/api/chat.postMessage

                ChatPostMessageResponse postResponse = slack.methods().chatPostMessage(
                        ChatPostMessageRequest.builder()
                                .token(token)
                                .channel(general.getId())
                                .text(body).username("twiliobot").iconEmoji(":taxi:")
                                .build());


            }


        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    //From Slack to User
    @RequestMapping(value = "/slack",method = RequestMethod.POST)
    public void receiveSMS(HttpServletRequest req){

        try {

            String body = req.getParameter("text").toString();
            String token = req.getParameter("token").toString();
            String username = req.getParameter("user_name").toString();


            if(token.equals(SLACK_WEBHOOK_SECRET)) {

                TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);

                // Build a filter for the MessageList
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("Body", username + ": " + body));
                params.add(new BasicNameValuePair("To", USER_NUMBER)); //Add real number here
                params.add(new BasicNameValuePair("From", TWILIO_NUMBER));

                MessageFactory messageFactory = client.getAccount().getMessageFactory();
                Message message = messageFactory.create(params);
                System.out.println(message.getSid());
            }

        }
        catch (Exception e) {
            //System.out.println(e.getErrorMessage());
            e.printStackTrace();
        }

    }

}
