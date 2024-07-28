package com.example.movies;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginFragment extends Fragment {

    private DatabaseHandler db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.log_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = new DatabaseHandler(requireContext());

        EditText usernameField = view.findViewById(R.id.username);
        EditText passwordField = view.findViewById(R.id.password);

        Button loginButton = view.findViewById(R.id.log_in);
        Button signupButton = view.findViewById(R.id.sign_up);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameField.getText().toString();
                String password = passwordField.getText().toString();

                if (isInputValid(username, password)) {
                    AppConfig.user = db.getUser(username);
                    AppConfig.loginName = username;

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();

                    Toast.makeText(requireContext(), "Добро пожаловать, " + username, Toast.LENGTH_SHORT).show();
                }

            }
        });


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.mainContainer, new SignupFragment())
                        .addToBackStack(null).commit();
            }
        });


    }

    private boolean isInputValid(String username, String password) {

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getActivity(), "Все поля обязательны для заполнения", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!db.checkUser(username)) {
            Toast.makeText(requireContext(), "Пользователь не зарегистрирован", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!db.checkUser(username, password)) {
            Toast.makeText(requireContext(), "Неверный пароль", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


}
