/*
 * Copyright 2017 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.flexbox

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.google.android.apps.flexbox.R
import com.google.android.flexbox.validators.*
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.lock_all.*
import org.json.JSONObject


/**
 * DialogFragment that changes the properties for a flex item.
 */
internal class FlexItemEditFragment : DialogFragment() {

    private var MY_PERMISSIONS_REQUEST_CALL_PHONE: Int = 1000

    private lateinit var alignSelfAuto: String

    private lateinit var alignSelfFlexStart: String

    private lateinit var alignSelfFlexEnd: String

    private lateinit var alignSelfCenter: String

    private lateinit var alignSelfBaseline: String

    private lateinit var alignSelfStretch: String

    private var viewIndex: Int = 0 // 초기 값 0 이지만 시작하면서 +1 하고 order 시작

    private lateinit var flexItem: FlexItem


    /**
     * Instance of a [FlexItem] being edited. At first it's created as another instance from
     * the [flexItem] because otherwise changes before clicking the ok button will be
     * reflected if the [flexItem] is changed directly.
     *
     *
     */
    private lateinit var flexItemInEdit: FlexItem

    private var flexItemChangedListener: FlexItemChangedListener? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog)
        } else {
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Dialog)
        }
        arguments?.let {
            flexItem = it.getParcelable(FLEX_ITEM_KEY)
            viewIndex = it.getInt(VIEW_INDEX_KEY)
        }
        flexItemInEdit = createNewFlexItem(flexItem)

        activity?.let {
            alignSelfAuto = it.getString(R.string.auto)
            alignSelfFlexStart = it.getString(R.string.flex_start)
            alignSelfFlexEnd = it.getString(R.string.flex_end)
            alignSelfCenter = it.getString(R.string.center)
            alignSelfBaseline = it.getString(R.string.baseline)
            alignSelfStretch = it.getString(R.string.stretch)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {




        val view = inflater.inflate(R.layout.client_status, container, false)
        dialog.setTitle((viewIndex + 1).toString())

        val context: Context = context!!

   //     val ct = container!!.context


        //전역 변수 선언 부분
        var mApp = GlobalVar()
        var server_address = mApp.server_address

        var tel=" "

        // viewIndex 값을 통해 환자 구분



        val back_icon: View = view.findViewById(R.id.back_icon)
        back_icon.setOnClickListener { view ->
            Snackbar.make(view, "인덱스 값 : "+(viewIndex+1) , Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()

            dismiss()

        }

        val profile_icon: View = view.findViewById(R.id.profile_icon)
        profile_icon.setOnClickListener { view ->
            Snackbar.make(view, "Here's a Snackbar menu", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()


            // 프로필 액티비티로 연결
            val intent = Intent (getActivity(), DetailProfileActivity::class.java)
            startActivity(intent)

            /*
            val view_profile = inflater.inflate(R.layout.detail_profile, container, false)
            dialog.setTitle((viewIndex + 1).toString())*/

        }

        val phone_icon: View = view.findViewById(R.id.phone_icon)
        phone_icon.setOnClickListener {

            /*view ->
            Snackbar.make(view, "Here's a Snackbar menu", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()*/


            //  추후 DB에서 전화번호 가져오도록 수정



            //user_st_json.php USER_TB json 으로 돌려주는 php
            val url = "http://"+server_address+"/saveme/user_st_json.php"
            //textView.text = ""

            // Post parameters
            // Form fields and values
            val params = HashMap<String,String>()
            //          params["foo1"] = "bar1"
            //        params["foo2"] = "bar2"
            //params["lock_st"] = "1"
            params["REG_ID"] = viewIndex.toString()
            val jsonObject = JSONObject(params)

            // Volley post request with parameters

            /*
            val request = StringRequest(Request.Method.POST,url,
                    Response.Listener { response ->
                        // Process the json
                        try {
                            textView.text = "Response: $response"

                        }catch (e:Exception){
                            textView.text = "Exception: $e"
                        }

                    }, Response.ErrorListener{
                // Error in request
                textView.text = "Volley error: $it"
            })*/


              val request = JsonObjectRequest(Request.Method.GET,url,jsonObject,
                      Response.Listener { response ->
                          // Process the json
                          try {
                             // textView.text = "Response: $response"
                             //context.toast("Response: $response")
                              val jsonarray=response.getJSONArray("USER_ST")

                          //   val jsonParser :JsonParser = JsonParser()
                          //    val jsonElement:JsonElement=jsonParser.parse(res)


                          //    context.toast("$tel")


                              for (i in 0..(jsonarray.length() - 1)) {
                                  val item = jsonarray.getJSONObject(i)

                                  val regid = item.getString("REG_ID")
                                  if(regid.toInt() == (viewIndex+1)){ // REG_ID 와 viewIdex를 비교하여 똑같은 값이면 TEL값 저장
                                      tel = item.getString("USER_TEL_NO")
                                       //context.toast("$tel")
                                      if(tel=="null"){
                                          context.toast("전화번호가 등록되어 있지 않습니다.")

                                      }else {

                                          context.toast("보호자 전화번호로 연결합니다.")

                                          val intent = Intent(Intent.ACTION_CALL);
                                          intent.data = Uri.parse("tel:" + tel)

                                          //====권한체크부분====//
                                          if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                              ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.CALL_PHONE), MY_PERMISSIONS_REQUEST_CALL_PHONE)
                                              //권한을 허용하지 않는 경우
                                          } else {
                                              //권한을 허용한 경우
                                              try {
                                                  startActivity(intent)
                                              } catch (e: SecurityException) {
                                                  e.printStackTrace()
                                              }

                                          }
                                      }
                                  }


                              }



                              /*
                              jsonResponse = "";
                              for (int i = 0; i < response.length(); i++) {

                                  JSONObject person = (JSONObject) response
                                          .get(i);

                                  String name = person.getString("name");
                                  String email = person.getString("email");
                                  JSONObject phone = person
                                          .getJSONObject("phone");
                                  String home = phone.getString("home");
                                  String mobile = phone.getString("mobile");

                                  jsonResponse += "Name: " + name + "\n\n";
                                  jsonResponse += "Email: " + email + "\n\n";
                                  jsonResponse += "Home: " + home + "\n\n";
                                  jsonResponse += "Mobile: " + mobile + "\n\n\n";

                              }*/



                          }catch (e:Exception){
                             // textView.text = "Exception: $e"
                            context.toast("Exception: $e")
                          }

                      }, Response.ErrorListener{
                  // Error in request
                  //textView.text = "Volley error: $it"
                  context.toast("Volley error: $it")
              })




            // Volley request policy, only one time request to avoid duplicate transaction
            request.retryPolicy = DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                    // 0 means no retry
                    0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
                    1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )

            // Add the volley post request to the request queue

                VolleySingleton.getInstance(context).addToRequestQueue(request)



            /*
            var number = tel
            context.toast("$tel")*/



        }

        val lock_icon: View = view.findViewById(R.id.lock_icon)
        lock_icon.setOnClickListener { view ->

            Snackbar.make(view, "잠금해제 되었습니다.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show()


            //user_st_json.php USER_TB json 으로 돌려주는 php
            val url = "http://"+server_address+"/saveme/lock_st_input.php?lock_st=0&regid="
            val url2 = url+(viewIndex+1)
  //          context.toast("잠금 해제 진행 중 입니다."+url2)




            val request = StringRequest(Request.Method.POST,url2,
                    Response.Listener { response ->
                        // Process the json
                        try {
                            context.toast("Response: $response")
                        }catch (e:Exception){
                            context.toast("Exception: $e")
                        }

                    }, Response.ErrorListener{
                // Error in request
                context.toast("Volley error: $it")
            })




            // Volley request policy, only one time request to avoid duplicate transaction
            request.retryPolicy = DefaultRetryPolicy(
                    DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                    // 0 means no retry
                    0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
                    1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )

            // Add the volley post request to the request queue

            VolleySingleton.getInstance(context).addToRequestQueue(request)



            //잠금 해제 관련 액티비티 실행
            /*
            val intent = Intent (getActivity(), LockActivity::class.java)
            startActivity(intent)
            */

        }


        /*
        val view = inflater.inflate(R.layout.fragment_flex_item_edit, container, false)
        dialog.setTitle((viewIndex + 1).toString())



        val context = activity ?: return view
        val orderTextInput: TextInputLayout = view.findViewById(R.id.input_layout_order) // input_layout_order 기존 order 값
        val orderEdit: EditText = view.findViewById(R.id.edit_text_order)   // 순서 수정되었는지 확인
        orderEdit.setText(flexItem.order.toString())
        orderEdit.addTextChangedListener(
                FlexEditTextWatcher(context, orderTextInput, IntegerInputValidator(),
                        R.string.must_be_integer))
        if (flexItem is FlexboxLayoutManager.LayoutParams) {
            // Order is not enabled in FlexboxLayoutManager
            orderEdit.isEnabled = false
        }









        val flexGrowInput: TextInputLayout = view .findViewById(R.id.input_layout_flex_grow)
        val flexGrowEdit: EditText = view.findViewById(R.id.edit_text_flex_grow)
        flexGrowEdit.setText(flexItem.flexGrow.toString())
        flexGrowEdit.addTextChangedListener(
                FlexEditTextWatcher(context, flexGrowInput, NonNegativeDecimalInputValidator(),
                        R.string.must_be_non_negative_float))

        val flexShrinkInput: TextInputLayout = view.findViewById(R.id.input_layout_flex_shrink)
        val flexShrinkEdit: EditText = view.findViewById(R.id.edit_text_flex_shrink)
        flexShrinkEdit.setText(flexItem.flexShrink.toString())
        flexShrinkEdit.addTextChangedListener(
                FlexEditTextWatcher(context, flexShrinkInput, NonNegativeDecimalInputValidator(),
                        R.string.must_be_non_negative_float))

        val flexBasisPercentInput: TextInputLayout =
                view.findViewById(R.id.input_layout_flex_basis_percent)
        val flexBasisPercentEdit: EditText = view.findViewById(R.id.edit_text_flex_basis_percent)
        if (flexItem.flexBasisPercent != FlexboxLayout.LayoutParams.FLEX_BASIS_PERCENT_DEFAULT) {
            flexBasisPercentEdit
                    .setText(Math.round(flexItem.flexBasisPercent * 100).toString())
        } else {
            flexBasisPercentEdit.setText(flexItem.flexBasisPercent.toInt().toString())
        }
        flexBasisPercentEdit.addTextChangedListener(
                FlexEditTextWatcher(context, flexBasisPercentInput, FlexBasisPercentInputValidator(),
                        R.string.must_be_minus_one_or_non_negative_integer))

        val widthInput: TextInputLayout = view.findViewById(R.id.input_layout_width)
        val widthEdit: EditText = view.findViewById(R.id.edit_text_width)
        widthEdit.setText(context.pixelToDp(flexItem.width).toString())
        widthEdit.addTextChangedListener(
                FlexEditTextWatcher(context, widthInput, DimensionInputValidator(),
                        R.string.must_be_minus_one_or_minus_two_or_non_negative_integer))

        val heightInput: TextInputLayout = view.findViewById(R.id.input_layout_height)
        val heightEdit: EditText= view.findViewById(R.id.edit_text_height)
        heightEdit.setText(context.pixelToDp(flexItem.height).toString())
        heightEdit.addTextChangedListener(
                FlexEditTextWatcher(context, heightInput, DimensionInputValidator(),
                        R.string.must_be_minus_one_or_minus_two_or_non_negative_integer))

        val minWidthInput: TextInputLayout = view.findViewById(R.id.input_layout_min_width)
        val minWidthEdit: EditText = view.findViewById(R.id.edit_text_min_width)
        minWidthEdit.setText(context.pixelToDp(flexItem.minWidth).toString())
        minWidthEdit.addTextChangedListener(
                FlexEditTextWatcher(context, minWidthInput, FixedDimensionInputValidator(),
                        R.string.must_be_non_negative_integer))

        val minHeightInput: TextInputLayout = view.findViewById(R.id.input_layout_min_height)
        val minHeightEdit: EditText = view.findViewById(R.id.edit_text_min_height)
        minHeightEdit.setText(context.pixelToDp(flexItem.minHeight).toString())
        minHeightEdit.addTextChangedListener(
                FlexEditTextWatcher(context, minHeightInput, FixedDimensionInputValidator(),
                        R.string.must_be_non_negative_integer))

        val maxWidthInput: TextInputLayout = view.findViewById(R.id.input_layout_max_width)
        val maxWidthEdit: EditText = view.findViewById(R.id.edit_text_max_width)
        maxWidthEdit.setText(context.pixelToDp(flexItem.maxWidth).toString())
        maxWidthEdit.addTextChangedListener(
                FlexEditTextWatcher(context, maxWidthInput, FixedDimensionInputValidator(),
                        R.string.must_be_non_negative_integer))

        val maxHeightInput: TextInputLayout = view.findViewById(R.id.input_layout_max_height)
        val maxHeightEdit: EditText = view.findViewById(R.id.edit_text_max_height)
        maxHeightEdit.setText(context.pixelToDp(flexItem.maxHeight).toString())
        maxHeightEdit.addTextChangedListener(
                FlexEditTextWatcher(context, maxHeightInput, FixedDimensionInputValidator(),
                        R.string.must_be_non_negative_integer))

        setNextFocusesOnEnterDown(orderEdit, flexGrowEdit, flexShrinkEdit, flexBasisPercentEdit,
                widthEdit, heightEdit, minWidthEdit, minHeightEdit, maxWidthEdit, maxHeightEdit)

        val alignSelfSpinner: Spinner = view.findViewById(R.id.spinner_align_self)
        val arrayAdapter = ArrayAdapter.createFromResource(activity,
                R.array.array_align_self, R.layout.spinner_item)
        alignSelfSpinner.adapter = arrayAdapter
        alignSelfSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, ignored: View?, position: Int,
                                        id: Long) {
                flexItemInEdit.alignSelf = when (parent.getItemAtPosition(position).toString()) {
                    alignSelfAuto -> AlignSelf.AUTO
                    alignSelfFlexStart -> AlignItems.FLEX_START
                    alignSelfFlexEnd -> AlignItems.FLEX_END
                    alignSelfCenter -> AlignItems.CENTER
                    alignSelfBaseline -> AlignItems.BASELINE
                    alignSelfStretch -> AlignItems.STRETCH
                    else -> return
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No op
            }
        }

        val wrapBeforeCheckBox: CheckBox = view.findViewById(R.id.checkbox_wrap_before)
        wrapBeforeCheckBox.isChecked = flexItem.isWrapBefore
        wrapBeforeCheckBox.setOnCheckedChangeListener { _, isChecked ->
            flexItemInEdit.isWrapBefore = isChecked }
        val alignSelfPosition = arrayAdapter
                .getPosition(alignSelfAsString(flexItem.alignSelf))
        alignSelfSpinner.setSelection(alignSelfPosition)

        view.findViewById<Button>(R.id.button_cancel).setOnClickListener {
            copyFlexItemValues(flexItem, flexItemInEdit)
            dismiss()
        }
        val okButton: Button = view.findViewById(R.id.button_ok)
        okButton.setOnClickListener(View.OnClickListener {
            if (orderTextInput.isErrorEnabled || flexGrowInput.isErrorEnabled ||
                    flexBasisPercentInput.isErrorEnabled || widthInput.isErrorEnabled ||
                    heightInput.isErrorEnabled || minWidthInput.isErrorEnabled ||
                    minHeightInput.isErrorEnabled || maxWidthInput.isErrorEnabled ||
                    maxHeightInput.isErrorEnabled) {
                Toast.makeText(activity, R.string.invalid_values_exist, Toast.LENGTH_SHORT)
                        .show()
                return@OnClickListener
            }
            if (flexItemChangedListener != null) {
                copyFlexItemValues(flexItemInEdit, flexItem)
                flexItemChangedListener!!.onFlexItemChanged(flexItem, viewIndex)
            }
            dismiss()
        })

        */
        return view
    }

    fun setFlexItemChangedListener(flexItemChangedListener: FlexItemChangedListener) {
        this.flexItemChangedListener = flexItemChangedListener
    }

    private fun setNextFocusesOnEnterDown(vararg textViews: TextView) {
        // This can be done by setting android:nextFocus* as in
        // https://developer.android.com/training/keyboard-input/navigation.html
        // But it requires API level 11 as a minimum sdk version. To support the lower level
        // devices,
        // doing it programmatically.
        for (i in textViews.indices) {
            textViews[i].setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_NEXT ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_NULL
                                && event.action == KeyEvent.ACTION_DOWN
                                && event.keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (i + 1 < textViews.size) {
                        textViews[i + 1].requestFocus()
                    } else if (i == textViews.size - 1) {
                        val inputMethodManager = activity?.getSystemService(
                                Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                        inputMethodManager?.hideSoftInputFromWindow(v.windowToken, 0)
                    }
                }
                true
            }

            // Suppress the key focus change by KeyEvent.ACTION_UP of the enter key
            textViews[i].setOnKeyListener { _, keyCode, event -> keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP }
        }

    }

    private fun alignSelfAsString(alignSelf: Int): String {
        return when (alignSelf) {
            AlignSelf.AUTO -> alignSelfAuto
            AlignItems.FLEX_START -> alignSelfFlexStart
            AlignItems.FLEX_END -> alignSelfFlexEnd
            AlignItems.CENTER -> alignSelfCenter
            AlignItems.BASELINE -> alignSelfBaseline
            AlignItems.STRETCH -> alignSelfStretch
            else -> alignSelfAuto
        }
    }



    // 실질적인 flexbox edit 실행 부분
    //

    /*

        val context = activity ?: return view
        val orderTextInput: TextInputLayout = view.findViewById(R.id.input_layout_order) // input_layout_order 기존 order 값
        val orderEdit: EditText = view.findViewById(R.id.edit_text_order)   // 순서 수정되었는지 확인
        orderEdit.setText(flexItem.order.toString())
        orderEdit.addTextChangedListener(
                FlexEditTextWatcher(context, orderTextInput, IntegerInputValidator(),
                        R.string.must_be_integer))
        if (flexItem is FlexboxLayoutManager.LayoutParams) {
            // Order is not enabled in FlexboxLayoutManager
            orderEdit.isEnabled = false
        }

     */



    //
    private inner class FlexEditTextWatcher internal constructor(val context: Context,
                                                                 val textInputLayout: TextInputLayout,
                                                                 val inputValidator: InputValidator,
                                                                 val errorMessageId: Int) : TextWatcher {

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // No op
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (inputValidator.isValidInput(s)) {
                textInputLayout.isErrorEnabled = false
                textInputLayout.error = ""
            } else {
                textInputLayout.isErrorEnabled = true
                textInputLayout.error = activity?.resources?.getString(errorMessageId)
            }
        }

        override fun afterTextChanged(editable: Editable) {
            if (textInputLayout.isErrorEnabled || editable.isEmpty() ||
                    !inputValidator.isValidInput(editable.toString())) {
                return
            }
            val value = editable.toString().toFloatOrNull() ?: return
            when (textInputLayout.id) {
                R.id.input_layout_order -> if (flexItemInEdit !is FlexboxLayoutManager.LayoutParams) {
                    flexItemInEdit.order = value.toInt()
                } else return
                R.id.input_layout_flex_grow -> flexItemInEdit.flexGrow = value
                R.id.input_layout_flex_shrink -> flexItemInEdit.flexShrink = value
                R.id.input_layout_width -> flexItemInEdit.width = context.dpToPixel(value.toInt())
                R.id.input_layout_height -> flexItemInEdit.height = context.dpToPixel(value.toInt())
                R.id.input_layout_flex_basis_percent -> if (value != FlexboxLayout.LayoutParams.FLEX_BASIS_PERCENT_DEFAULT) {
                    flexItemInEdit.flexBasisPercent = value.toInt() / 100.0f
                } else {
                    flexItemInEdit.flexBasisPercent = FlexItem.FLEX_BASIS_PERCENT_DEFAULT
                }
                R.id.input_layout_min_width -> flexItemInEdit.minWidth = context.dpToPixel(value.toInt())
                R.id.input_layout_min_height -> flexItemInEdit.minHeight = context.dpToPixel(value.toInt())
                R.id.input_layout_max_width -> flexItemInEdit.maxWidth = context.dpToPixel(value.toInt())
                R.id.input_layout_max_height -> flexItemInEdit.maxHeight = context.dpToPixel(value.toInt())
                else -> return
            }
        }
    }

    //새로운 flexitem 생성시 호출 되는 함수
    private fun createNewFlexItem(item: FlexItem): FlexItem {
        if (item is FlexboxLayout.LayoutParams) {
            val newItem = FlexboxLayout.LayoutParams(item.getWidth(), item.getHeight())
            copyFlexItemValues(item, newItem)
            return newItem
        } else if (item is FlexboxLayoutManager.LayoutParams) {
            val newItem = FlexboxLayoutManager.LayoutParams(item.getWidth(), item.getHeight())
            copyFlexItemValues(item, newItem)
            return newItem
        }
        throw IllegalArgumentException("Unknown FlexItem: $item")
    }


    //item 위치 이동시 사용
    private fun copyFlexItemValues(from: FlexItem, to: FlexItem) {
        if (from !is FlexboxLayoutManager.LayoutParams) {
            to.order = from.order
        }
        to.flexGrow = from.flexGrow
        to.flexShrink = from.flexShrink
        to.flexBasisPercent = from.flexBasisPercent
        to.height = from.height
        to.width = from.width
        to.maxHeight = from.maxHeight
        to.minHeight = from.minHeight
        to.maxWidth = from.maxWidth
        to.minWidth = from.minWidth
        to.alignSelf = from.alignSelf
        to.isWrapBefore = from.isWrapBefore
    }

    companion object {

        private const val FLEX_ITEM_KEY = "flex_item"

        private const val VIEW_INDEX_KEY = "view_index"  // view index 값 저장

        fun newInstance(flexItem: FlexItem, viewIndex: Int) = FlexItemEditFragment().apply {
            arguments = Bundle().apply {
                putParcelable(FLEX_ITEM_KEY, flexItem)
                putInt(VIEW_INDEX_KEY, viewIndex)
            }
        }
    }
}


