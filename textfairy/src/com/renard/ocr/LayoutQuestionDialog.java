package com.renard.ocr;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.renard.ocr.help.OCRLanguageActivity;
import com.renard.ocr.help.OCRLanguageAdapter.OCRLanguage;
import com.renard.util.PreferencesUtils;

public class LayoutQuestionDialog {

	public enum LayoutKind {
		SIMPLE, COMPLEX, DO_NOTHING;
	}

	private static LayoutKind mLayout = LayoutKind.SIMPLE;
	private static String mLanguage;

	public interface LayoutChoseListener {
		void onLayoutChosen(final LayoutKind layoutKind, final String language);
	}

	public static AlertDialog createDialog(final Context context,
			final LayoutChoseListener listener) {

		mLayout = LayoutKind.SIMPLE;
		Pair<String, String> language = PreferencesUtils
				.getOCRLanguage(context);
		mLanguage = language.first;

		AlertDialog.Builder builder;

		// builder = new AlertDialog.Builder(new ContextThemeWrapper(context,
		// R.style.Theme_Sherlock_Light_Dialog));
		builder = new AlertDialog.Builder(context);
		builder.setCancelable(false);
		View layout = View.inflate(context, R.layout.dialog_layout_question,
				null);
		builder.setView(layout);

		final TextView speech = (TextView) layout.findViewById(R.id.fairy_text);
		speech.setText(context.getString(R.string.document_layout_dialog));
		final ImageView fairyImage = (ImageView) layout
				.findViewById(R.id.fairy_image);
		fairyImage.setImageResource(R.drawable.fairy_question);
		final RadioGroup layoutRadioGroup = (RadioGroup) layout
				.findViewById(R.id.radioGroup_layout_buttons);
		layoutRadioGroup.check(R.id.radio_simple);

		final Button langButton = (Button) layout
				.findViewById(R.id.button_language);
		langButton.setText(language.second);
		langButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ChooseLanguageDialog.createDialog(context,
						new ChooseLanguageDialog.OnLanguageChosenListener() {

							@Override
							public void onLanguageChosen(OCRLanguage lang) {
								mLanguage = lang.getValue();
								langButton.setText(lang.getDisplayText());
								PreferencesUtils.saveOCRLanguage(context, lang);
							}
						}).show();

			}
		});

		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						final String lang;
						final String english = "eng";
						if (!mLanguage.equals(english)) {
							lang = mLanguage + "+" + english;
						} else {
							lang = mLanguage;
						}
						int checked = layoutRadioGroup
								.getCheckedRadioButtonId();
						switch (checked) {
						case R.id.radio_complex:
							mLayout = LayoutKind.COMPLEX;
							break;
						case R.id.radio_no_ocr:
							mLayout = LayoutKind.DO_NOTHING;
							break;
						case R.id.radio_simple:
							mLayout = LayoutKind.SIMPLE;
							break;
						}
						listener.onLayoutChosen(mLayout, lang);

					}
				});

		builder.setNegativeButton(R.string.cancel,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		final AlertDialog dialog = builder.create();

		final Button downloadButton = (Button) layout
				.findViewById(R.id.button_load_language);
		downloadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(context, OCRLanguageActivity.class);
				context.startActivity(i);
				dialog.cancel();
			}
		});

		return dialog;

	}
}
