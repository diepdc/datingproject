package com.example.hoang.datingproject.Utilities;

import com.example.hoang.datingproject.Model.FeedModel;
import com.example.hoang.datingproject.Model.FootPrintModel;
import com.example.hoang.datingproject.Model.MessageModel;
import com.example.hoang.datingproject.Model.PersonModel;

import java.util.ArrayList;

/**
 * Created by hoang on 6/13/2016.
 */
public interface AsyncRespond {
    void processFinish(ArrayList<FeedModel> arr);
    void processFriendFinish(ArrayList<PersonModel> arr);
    void processMessageFinish(ArrayList<MessageModel> arr);
    void processDatingFinish(ArrayList<FootPrintModel> arr);
    void processLastFinish(ArrayList<FeedModel> arr);
    void processProfileFinish(String name, String address, String description, String profileImage);
}
