/*
 * Copyright (C) 2018 skydoves
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("unused")

package com.skydoves.colorpickerpreference

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class ColorPickerPreference : Preference {

  private lateinit var colorBox: View
  private lateinit var preferenceDialog: AlertDialog
  private lateinit var colorPickerView: ColorPickerView

  private var defaultColor: Int = Color.BLACK
  private var paletteDrawable: Drawable? = null
  private var selectorDrawable: Drawable? = null
  private var title: String? = null
  private var positive: String? = null
  private var negative: String? = null
  private var isAttachAlphaSlideBar = true
  private var isAttachBrightnessSlideBar = true

  constructor(context: Context) : super(context)

  constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    getAttrs(attrs)
    onInit()
  }

  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs,
    defStyleAttr) {
    getAttrs(attrs, defStyleAttr)
    onInit()
  }

  private fun getAttrs(attrs: AttributeSet) {
    val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerPreference)
    setTypeArray(typedArray)
  }

  private fun getAttrs(attrs: AttributeSet, defStyle: Int) {
    val typedArray =
      context.obtainStyledAttributes(attrs, R.styleable.ColorPickerPreference, defStyle, 0)
    setTypeArray(typedArray)
  }

  private fun setTypeArray(typedArray: TypedArray) {
    defaultColor =
      typedArray.getColor(R.styleable.ColorPickerPreference_default_color, defaultColor)
    paletteDrawable = typedArray.getDrawable(R.styleable.ColorPickerPreference_preference_palette)
    selectorDrawable = typedArray.getDrawable(R.styleable.ColorPickerPreference_preference_selector)
    title = typedArray.getString(R.styleable.ColorPickerPreference_preference_dialog_title)
    positive = typedArray.getString(R.styleable.ColorPickerPreference_preference_dialog_positive)
    negative = typedArray.getString(R.styleable.ColorPickerPreference_preference_dialog_negative)
    isAttachAlphaSlideBar =
      typedArray.getBoolean(R.styleable.ColorPickerPreference_preference_attachAlphaSlideBar,
        isAttachAlphaSlideBar)
    isAttachBrightnessSlideBar =
      typedArray.getBoolean(R.styleable.ColorPickerPreference_preference_attachBrightnessSlideBar,
        isAttachBrightnessSlideBar)
  }

  fun getPreferenceDialog(): AlertDialog {
    return this.preferenceDialog
  }

  fun getColorPickerView(): ColorPickerView {
    return colorPickerView
  }

  private fun onInit() {
    widgetLayoutResource = R.layout.layout_colorpicker_preference
    preferenceDialog = ColorPickerDialog.Builder(context).apply {
      setTitle(title)
      setPositiveButton(positive,
        ColorEnvelopeListener { envelope, _ ->
          colorBox.setBackgroundColor(envelope.color)
          preferenceManager
            .sharedPreferences
            .edit()
            .putInt(key, envelope.color)
            .apply()
        })
      setNegativeButton(negative) { dialogInterface, _ -> dialogInterface.dismiss() }
      attachAlphaSlideBar(isAttachAlphaSlideBar)
      attachBrightnessSlideBar(isAttachBrightnessSlideBar)
      this@ColorPickerPreference.colorPickerView = this.colorPickerView.apply {
        paletteDrawable?.let { setPaletteDrawable(it) }
        selectorDrawable?.let { setSelectorDrawable(it) }
        preferenceName = key
      }
    }.create()
  }

  override fun onBindViewHolder(holder: PreferenceViewHolder) {
    super.onBindViewHolder(holder)
    colorBox = holder.findViewById(R.id.colorpicker_preference_colorbox)
    if (key != null) {
      colorBox.setBackgroundColor(
        preferenceManager.sharedPreferences.getInt(key, defaultColor))
    } else {
      colorBox.setBackgroundColor(defaultColor)
    }
  }

  override fun onClick() {
    super.onClick()
    preferenceDialog.show()
  }
}
