package com.gunit.spacecrack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.ProfilePictureView;

import java.util.List;

/**
 * Created by Dimi on 7/02/14.
 */
public class HomeFragment extends Fragment {
    private ProfilePictureView profilePictureView;
    private TextView name;
    private SpaceCrackApplication application;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        profilePictureView = (ProfilePictureView) view.findViewById(R.id.ppv_home_profilepicture);
        name = (TextView) view.findViewById(R.id.txt_home_welcome);

        updateAccount();

        return view;
    }

    //Update account with information retrieved from Facebook
    private void updateAccount() {
        final Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null && session == Session.getActiveSession()) {
                        profilePictureView.setProfileId(user.getId());
                        name.setText(user.getFirstName() + " " + user.getLastName());
                        //getFriends();
                    }
                }
            });
            request.executeAsync();
        }
    }

    //Get the friendslist
    private void getFriends(){
        Session activeSession = Session.getActiveSession();
        if(activeSession.getState().isOpened()){
            Request friendRequest = Request.newMyFriendsRequest(activeSession,
                    new Request.GraphUserListCallback(){
                        @Override
                        public void onCompleted(List<GraphUser> users,
                                                Response response) {
                            Log.i("INFO", response.toString());

                        }
                    });
            Bundle params = new Bundle();
            params.putString("fields", "id,name,picture");
            friendRequest.setParameters(params);
            friendRequest.executeAsync();
        }
    }
}
