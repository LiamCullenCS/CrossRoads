package com.kitkat.crossroads.Jobs;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kitkat.crossroads.Account.LoginActivity;
import com.kitkat.crossroads.R;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PostAnAdvertFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PostAnAdvertFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostAnAdvertFragment extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private static final String TAG = "PostAnActivityFragment";

    private DataSnapshot jobReference;


    private FirebaseAuth auth;

    private EditText editTextAdName, editTextAdDescription, editTextColDate, editTextColTime;
    private EditText editTextColAddL1, editTextColAddL2, editTextColAddTown, editTextColAddPostcode;
    private EditText editTextDelAddL1, editTextDelAddL2, editTextDelAddTown, editTextDelAddPostcode;
    private Spinner editTextJobSize, editTextJobType;
    private ScrollView scrollView;

    private Button buttonPostAd;

    private DatabaseReference databaseReference;

    public PostAnAdvertFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostAnAdvertFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostAnAdvertFragment newInstance(String param1, String param2)
    {
        PostAnAdvertFragment fragment = new PostAnAdvertFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_post_an_advert, container, false);
        // Inflate the layout for this fragment

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null)
        {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = auth.getCurrentUser();

        buttonPostAd = (Button) view.findViewById(R.id.buttonAddJob);

        scrollView = (ScrollView) view.findViewById(R.id.advertScrollView);

        editTextAdName = (EditText) view.findViewById(R.id.editTextAdName);
        editTextAdDescription = (EditText) view.findViewById(R.id.editTextAdDescription);
        editTextJobSize = (Spinner) view.findViewById(R.id.editTextJobSize);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.job_sizes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editTextJobSize.setAdapter(adapter);
        editTextJobType = (Spinner) view.findViewById(R.id.editTextJobType);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.job_types, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editTextJobType.setAdapter(adapter1);
        editTextColDate = (EditText) view.findViewById(R.id.editTextJobColDate);
        editTextColDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        dateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {

                month = month + 1;
                Log.d(TAG, "onDateSet: date: " + year + "/" + month + "/" + dayOfMonth);

                if (dayOfMonth >= 1 && dayOfMonth <= 9)
                {
                    String newDay = "0" + dayOfMonth;
                    editTextColDate.setText(newDay + "/" + month + "/" + year);
                }

                if (month >= 1 && month <= 9)
                {
                    String newMonth = "0" + month;
                    editTextColDate.setText(dayOfMonth + "/" + newMonth + "/" + year);
                }

                if (dayOfMonth >= 1 && dayOfMonth <= 9 && month >= 1 && month <= 9)
                {
                    String newDay = "0" + dayOfMonth;
                    String newMonth = "0" + month;
                    editTextColDate.setText(newDay + "/" + newMonth + "/" + year);
                } else
                {
                    editTextColDate.setText(dayOfMonth + "/" + month + "/" + year);
                }
            }
        };
        editTextColTime = (EditText) view.findViewById(R.id.editTextJobColTime);
        editTextColTime.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute)
                    {
                        editTextColTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
        editTextColAddL1 = (EditText) view.findViewById(R.id.editTextJobColL1);
        editTextColAddL2 = (EditText) view.findViewById(R.id.editTextJobColL2);
        editTextColAddTown = (EditText) view.findViewById(R.id.editTextJobColTown);
        editTextColAddPostcode = (EditText) view.findViewById(R.id.editTextJobColPostcode);
        editTextDelAddL1 = (EditText) view.findViewById(R.id.editTextJobDelL1);
        editTextDelAddL2 = (EditText) view.findViewById(R.id.editTextJobDelL2);
        editTextDelAddTown = (EditText) view.findViewById(R.id.editTextJobDelTown);
        editTextDelAddPostcode = (EditText) view.findViewById(R.id.editTextJobDelPostcode);
        buttonPostAd.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {


                if (TextUtils.isEmpty(editTextAdName.getText()))
                {
                    editTextAdName.setText("");
                    editTextAdName.setHintTextColor(Color.RED);
                    editTextAdName.setHint("Please enter Advert Name!");
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
                if (TextUtils.isEmpty(editTextAdDescription.getText()))
                {
                    editTextAdDescription.setText("");
                    editTextAdDescription.setHintTextColor(Color.RED);
                    editTextAdDescription.setHint("Please enter Advert Description!");
                    scrollView.fullScroll(ScrollView.FOCUS_UP);

                }
                if (TextUtils.isEmpty(editTextColDate.getText()))
                {
                    editTextColDate.setText("");
                    editTextColDate.setHintTextColor(Color.RED);
                    editTextColDate.setHint("Please enter a Collection Date!");
                    scrollView.fullScroll(ScrollView.FOCUS_UP);

                }
                if (TextUtils.isEmpty(editTextColTime.getText()))
                {
                    editTextColTime.setText("");
                    editTextColTime.setHintTextColor(Color.RED);
                    editTextColTime.setHint("Please enter a Collection Time!");
                    scrollView.fullScroll(ScrollView.FOCUS_UP);

                }
                if (TextUtils.isEmpty(editTextColAddL1.getText()))
                {
                    editTextColAddL1.setText("");
                    editTextColAddL1.setHintTextColor(Color.RED);
                    editTextColAddL1.setHint("Please enter Address Line 1!");
                    scrollView.fullScroll(ScrollView.FOCUS_UP);

                }
                if (TextUtils.isEmpty(editTextColAddTown.getText()))
                {
                    editTextColAddTown.setText("");
                    editTextColAddTown.setHintTextColor(Color.RED);
                    editTextColAddTown.setHint("Please enter a Town!");
                    scrollView.fullScroll(ScrollView.FOCUS_UP);

                }
                if ((!(editTextColAddPostcode.getText().toString().matches("^([A-PR-UWYZ](([0-9](([0-9]|[A-HJKSTUW])?)?)|([A-HK-Y][0-9]([0-9]|[ABEHMNPRVWXY])?)) ?[0-9][ABD-HJLNP-UW-Z]{2})$"))) || (TextUtils.isEmpty(editTextColAddPostcode.getText())))
                {
                    editTextColAddPostcode.setText("");
                    editTextColAddPostcode.setHintTextColor(Color.RED);
                    editTextColAddPostcode.setHint("Please enter valid Postcode!");
                    scrollView.fullScroll(ScrollView.FOCUS_UP);

                }
                if (TextUtils.isEmpty(editTextDelAddL1.getText()))
                {
                    editTextDelAddL1.setText("");
                    editTextDelAddL1.setHintTextColor(Color.RED);
                    editTextDelAddL1.setHint("Please enter Address Line 1!");
                    scrollView.fullScroll(ScrollView.FOCUS_UP);

                }
                if (TextUtils.isEmpty(editTextDelAddTown.getText()))
                {
                    editTextDelAddTown.setText("");
                    editTextDelAddTown.setHintTextColor(Color.RED);
                    editTextDelAddTown.setHint("Please enter a Town!");
                    scrollView.fullScroll(ScrollView.FOCUS_UP);

                }
                if ((!(editTextDelAddPostcode.getText().toString().matches("^([A-PR-UWYZ](([0-9](([0-9]|[A-HJKSTUW])?)?)|([A-HK-Y][0-9]([0-9]|[ABEHMNPRVWXY])?)) ?[0-9][ABD-HJLNP-UW-Z]{2})$"))) || (TextUtils.isEmpty(editTextDelAddPostcode.getText())))
                {
                    editTextDelAddPostcode.setText("");
                    editTextDelAddPostcode.setHintTextColor(Color.RED);
                    editTextDelAddPostcode.setHint("Please enter valid Postcode!");
                    scrollView.fullScroll(ScrollView.FOCUS_UP);

                } else
                {
                    saveJobInformation();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content, new FindAJobFragment()).addToBackStack("tag").commit();
                }
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
        {

        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void saveJobInformation()
    {
        String adName = editTextAdName.getText().toString().trim();
        String adDescription = editTextAdDescription.getText().toString().trim();
        String jobSize = editTextJobSize.getSelectedItem().toString().trim();
        String jobType = editTextJobType.getSelectedItem().toString().trim();
        String colDate = editTextColDate.getText().toString().trim();
        String colTime = editTextColTime.getText().toString().trim();
        String colL1 = editTextColAddL1.getText().toString().trim();
        String colL2 = editTextColAddL2.getText().toString().trim();
        String colTown = editTextColAddTown.getText().toString().trim();
        String colPostcode = editTextColAddPostcode.getText().toString().trim().toUpperCase();
        String delL1 = editTextDelAddL1.getText().toString().trim();
        String delL2 = editTextDelAddL2.getText().toString().trim();
        String delTown = editTextDelAddTown.getText().toString().trim();
        String delPostcode = editTextDelAddPostcode.getText().toString().trim().toUpperCase();

        String jobStatus = "Pending";
        String courierID = " ";

        FirebaseUser user = auth.getCurrentUser();

        String posterID = user.getUid().toString().trim();

        final JobInformation jobInformation = new JobInformation(adName, adDescription, jobSize, jobType, posterID,
                courierID, colDate, colTime, colL1, colL2, colTown, colPostcode, delL1, delL2, delTown, delPostcode, jobStatus);


        databaseReference.child("Jobs").push().setValue(jobInformation);

        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                jobReference = dataSnapshot.child("Jobs");

                Iterable<DataSnapshot> jobListSnapShot = jobReference.getChildren();

                for (DataSnapshot ds : jobListSnapShot)
                {
                    JobInformation j = ds.getValue(JobInformation.class);
                    if(j.equals(jobInformation))
                    {
                        databaseReference.child("Jobs").child(ds.getKey()).child("jobID").setValue(ds.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });


        Toast.makeText(getActivity(), "Job Added!", Toast.LENGTH_SHORT).show();


    }
}