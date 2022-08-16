package com.example.park.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.park.MyViewModel;
import com.example.park.R;
import com.example.park.databinding.FragmentCreditCardBinding;
import com.example.park.models.CreditCard;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CreditCardFragment extends Fragment {

    private FragmentCreditCardBinding mBinding;
    private TextInputLayout obCreditCardNumber;
    private TextInputLayout obExpirationDate;
    private TextInputLayout obClientId;


    private TextInputLayout obCvv;
    private TextInputLayout obFirstName;
    private TextInputLayout obLastName;
    private TextInputEditText etCreditCardNumber;
    private TextInputEditText etExpirationDate;
    private TextInputEditText etCvv;
    private TextInputEditText etFirstName;
    private TextInputEditText etLastName;
    private TextInputEditText etIdClient;
    private MyViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentCreditCardBinding.inflate(inflater, container, false);
        obCreditCardNumber = mBinding.creditCardInputPanel.tilCreditCardNumber;
        obExpirationDate = mBinding.creditCardInputPanel.tilExpirationDate;
        obCvv = mBinding.creditCardInputPanel.tilCvv;
        obFirstName = mBinding.creditCardInputPanel.tilFirstName;
        obLastName = mBinding.creditCardInputPanel.tilLastName;
        obClientId = mBinding.creditCardInputPanel.tilIdOfClient;
        etCreditCardNumber = mBinding.creditCardInputPanel.etCreditCardNumber;
        etExpirationDate = mBinding.creditCardInputPanel.etExpirationDate;
        etCvv = mBinding.creditCardInputPanel.etCvv;
        etFirstName = mBinding.creditCardInputPanel.etFirstName;
        etIdClient = mBinding.creditCardInputPanel.etIdOfClient;
        etLastName = mBinding.creditCardInputPanel.etLastName;
        mBinding.creditCardInputPanel.btnSubmitPayment.setOnClickListener(v -> validateCard(v));
        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        viewModel.getCard(mBinding.progressBar).observe(getViewLifecycleOwner(), creditCard -> {
            if (creditCard != null) {
                etIdClient.setText(creditCard.getIdClient());
                etCreditCardNumber.setText(creditCard.getCreditCardNumber());
                etExpirationDate.setText(creditCard.getExpirationDate());
                etCvv.setText(creditCard.getCvv());
                etFirstName.setText(creditCard.getFirstName());
                etLastName.setText(creditCard.getLastName());
                mBinding.creditCardInputPanel.btnSubmitPayment.setText(getString(R.string.edit_credit_card));
            }
        });
        return mBinding.getRoot();
    }

    public void validateCard(View view) {
        clearAnyPreviousErrorMessages();
        String creditCardNumber = etCreditCardNumber.getText().toString();
        String clientId = etIdClient.getText().toString();
        String expirationDate = etExpirationDate.getText().toString();
        String cvv = etCvv.getText().toString();
        String firstName = CreditCard.cleanName(etFirstName.getText().toString());
        etFirstName.setText(firstName);
        String lastName = CreditCard.cleanName(etLastName.getText().toString());
        etLastName.setText(lastName);

        if (!CreditCard.isValidCardNumber(creditCardNumber)) {
            obCreditCardNumber.setError(getString(R.string.invalid_card_number));
            etCreditCardNumber.requestFocus();
        } else if (!CreditCard.isValidExpirationDate(expirationDate)) {
            obExpirationDate.setError(getString(R.string.invalid_expiration_date));
            etExpirationDate.requestFocus();
        } else if (!CreditCard.isValidCvv(creditCardNumber, cvv)) {
            obCvv.setError(getString(R.string.invalid_cvv));
            etCvv.requestFocus();
        } else if (firstName.isEmpty()) {
            obFirstName.setError(getString(R.string.please_enter_first_name));
            etFirstName.requestFocus();
        } else if (lastName.isEmpty()) {
            obLastName.setError(getString(R.string.please_enter_last_name));
            etLastName.requestFocus();
        } else if (!CreditCard.isClientIdValid(clientId)) {
            obClientId.setError(getString(R.string.please_enter_id));
            obClientId.requestFocus();
        } else {
            closeSoftKeyboard(view);
            CreditCard creditCard = new CreditCard(creditCardNumber, expirationDate, cvv, firstName, lastName, clientId);
            submitCreditCard(creditCard);
        }
    }

    private void submitCreditCard(CreditCard creditCard) {
        showProgressBar(true);
        viewModel.addCard(creditCard).observe(getViewLifecycleOwner(), result -> {
            showProgressBar(false);
            if (result) {
                Toast.makeText(requireContext(), getString(R.string.successful_add_card), Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            } else {
                Toast.makeText(requireContext(), getResources().getString(R.string.failed_add_card), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showProgressBar(boolean visible) {
        mBinding.progressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void clearAnyPreviousErrorMessages() {
        obCreditCardNumber.setError(null);
        obExpirationDate.setError(null);
        obCvv.setError(null);
        obFirstName.setError(null);
        obLastName.setError(null);
    }

    public void closeSoftKeyboard(View view) {
        // Don't have the soft keyboard taking up screen space after tapping the button
        InputMethodManager inputManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    public static CreditCardFragment newInstance() {
        return new CreditCardFragment();
    }

}