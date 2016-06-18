package com.example.dmitro.chat_firebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.dmitro.chat_firebase.adapter.Message;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static final int DEFAULT_MSG_LENGTH_LIMIT = 10;
    public static final String MESSAGES_CHILD = "messages";
    private static final int LAYOUT = R.layout.activity_main;
    //TAG
    private static final String TAG = "MainActivity";
//    public static final String ANONYMOUS = "Anonymous";

    // Firebase instance variables
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<Message, MessageViewHolder> firebaseRecyclerAdapter;

    private String userName;
    private String photoUrl;
    private GoogleApiClient googleApiClient;

    private RecyclerView messageRecyclerView;
    private LinearLayoutManager layoutManager;

    private EditText messageEditText;
    private Button sendButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

//        userName = ANONYMOUS; /* Set default username is anonymous.*/

        initFirebaseAuth();
        initGoogleApi();
        initRecyclerView();

//        initMessageEditText();

//        initSendButton();

    }

    private void initGoogleApi() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                firebaseAuth.signOut();
                Auth.GoogleSignInApi.signOut(googleApiClient);
//                userName = ANONYMOUS;
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initSendButton() {
        sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initMessageEditText() {
        messageEditText = (EditText) findViewById(R.id.message_edit_text);
        messageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                sharedPreferences.getInt("friendly_msg_length"/*какойто клас*/, DEFAULT_MSG_LENGTH_LIMIT))});
        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    sendButton.setEnabled(true);
                } else {
                    sendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initRecyclerView() {
        messageRecyclerView = (RecyclerView) findViewById(R.id.message_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Message, MessageViewHolder>(
                Message.class,
                R.layout.item_massage,
                MessageViewHolder.class,
                databaseReference.child(MESSAGES_CHILD)
        ) {
            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder, Message message, int position) {
                viewHolder.messengerName.setText(message.getName());
                viewHolder.massageText.setText(message.getText());
                if (message.getPhotoUrl() != null) {
                    Glide.with(MainActivity.this).load(message.getPhotoUrl()).into(viewHolder.messengerAvatar);
                }
            }
        };
        firebaseRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int messageCount = firebaseRecyclerAdapter.getItemCount();
                int lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the user is at the bottom of the list, scroll
                // to the bottom of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (messageCount - 1) && lastVisiblePosition == (positionStart - 1))) {
                    messageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        messageRecyclerView.setLayoutManager(layoutManager);
        messageRecyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void initFirebaseAuth() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        } else {
            userName = firebaseUser.getDisplayName();
            if (firebaseUser.getPhotoUrl() != null) {
                photoUrl = firebaseUser.getPhotoUrl().toString();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        public ImageView messengerAvatar;
        public TextView messengerName;
        public TextView massageText;

        public MessageViewHolder(View itemView) {
            super(itemView);
            messengerAvatar = (ImageView) itemView.findViewById(R.id.messenger_avatar);
            messengerName = (TextView) itemView.findViewById(R.id.messenger_name);
            massageText = (TextView) itemView.findViewById(R.id.massage_text);
        }
    }
}
