package com.pidpia.delivery;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by jenorain on 2016-12-07.
 */

public class User {

    public SharedPreferences prefs;
    public SharedPreferences.Editor editor;
    Context mContext;
    public String UUID;
    public String SSID;
    public String TID;

    final static String URL_DOMAIN = "http://110.10.189.232:8080/";
    final static String URL_LOGIN = "dv_login.php";
    final static String UR_MAIN_INIT = "dv_main.php";
    final static String URL_ORDER_LIST = "dv_order_list.php";
    final static String URL_ORDER_DETAIL = "dv_order_detail.php";
    final static String URL_ORDER_DATE_SET = "dv_order_date_set.php";
    final static String URL_LOCATION_SET = "dv_order_locatin_set.php";
    final static String URL_ORDER_SET = "dv_order_delivery_set.php";
    final static String URL_SIGN_SET = "dv_delivery_sign1.php";
    final static String URL_ORDER_FEED_LIST = "dv_order_feed_list.php";
    final static String URL_ORDER_FEED_SET = "dv_order_feed_set.php";

    final static String info_ok = " 배송기사 개인정보 취급방침\n\n" +
            " 피드피아(이하 본사)는 배송기사회원 여러분들 개인정보를 안전하게 취급하기 위해 아래와 같은 방침을 세웠습니다. 여러분의 개인정보는 서비스의 원활한 제공을 위해서 여러분이 동의한 목적과 범위 내에서만 이용됩니다. 법령에 의거하거나 여러분이 별도로 동의하지 않는 한 본사는 여러분의 개인정보를 제3자에게 제공하는 일은 없습니다. 안심하셔도 좋습니다. \n" +
            " \n" +
            " \n1. 수집 목적 및 범위\n" +
            " 개인정보는 서비스의 원활한 이용을 목적으로 여러분이 동의한 범위 내에서만 이용됩니다. 본사가 필요로 하는 개인정보와 그 목적은 다음과 같습니다. \n" +
            " 1) 배송기사님의 정보표시를 위해서 사료회사가 등록한 배송기사님의 ①이름, ②연락처, ③배송차량번호를 수집합니다.\n" +
            " 2) 배송정보표시를 위해 ①현재 위치, ②배정된 배송목록, ③배송출발시간 ④배송도착시간을 수집합니다. 현재 위치는 ‘배송중’인 주문이 있을 경우에만 수집됩니다.\n" +
            " 3) 서비스 이용과정이나 사업처리과정에서 ①IP정보, ②쿠키, ③이용기록, ④불량 이용기록, ⑤단말기정보가 수집됩니다.\n" +
            " \n2. 제 3자에게 위탁하지 않습니다.\n" +
            "만일 법령에 위반되거나 여러분이 별도로 동의하지 않는 경우, 본사는 절대로 제 3자에게 여러분의 개인정보를 제공하지 않습니다. 단, 농가회원에게 이용문의 및 사료판매를 위해 일부 정보를 제공합니다. 그 내용은 다음과 같습니다.\n" +
            "· 제공받는 자: 농가회원, 사료회사\n" +
            "· 이용목적: 배송현황 정보 제공\n" +
            "· 제공항목: ①이름, ②연락처, ③배송차량번호, ④현재 위치(배송 중에만), ⑤출발 및 도착시간\n" +
            "· 보유 및 이용기간: 사료회사 회원의 탈퇴 또는 배송기사계정 삭제(단, 배송기록은 법령에 의거한 기간까지 제공)\n" +
            " \n3. 다만, 원활한 서비스 제공을 위해 개인정보를 위탁하기도 합니다.\n" +
            "본사는 서비스의 원활한 제공을 위해 필요한 때에는 개인정보의 취급을 일부 위탁하고 있습니다. 위탁처리 기관 및 위탁업무의 내용은 아래를 참조해 주세요.\n" +
            "· 수탁업체: FORUTONA\n" +
            "· 위탁내용: 서비스 개발 및 운영\n" +
            "· 개인정보의 보유 및 이용기간: 회원탈퇴 시 혹은 위탁계약 종료 시까지\n" +
            " \n4. 보유기간\n" +
            " 본사는 여러분의 개인정보를 이용 목적을 위해 한시적으로 보유합니다. 본사가 법령에 따라 보관하는 개인정보 및 해당 법령은 아래 표와 같습니다.\n" +
            "· 보존 항목: 계약 또는 청약철회 등에 관한 기록\n" +
            "· 근거 법령: 전자상거래 등에서의 소비자보호에 관한 법률\n" +
            "· 보존 기간: 5년\n" +
            "· 보존 항목: 대금결제 및 재화 등의 공급에 관한 기록\n" +
            "· 근거 법령: 전자상거래 등에서의 소비자보호에 관한 법률\n" +
            "· 보존 기간: 5년\n" +
            "· 보존 항목: 소비자의 불만 또는 분쟁처리에 관한 기록\n" +
            "· 근거 법령: 전자상거래 등에서의 소비자보호에 관한 법률\n" +
            "· 보존 기간: 3년\n" +
            "· 보존 항목: 표시/광고에 관한 기록\n" +
            "· 근거 법령: 전자상거래 등에서의 소비자보호에 관한 법률\n" +
            "· 보존 기간: 6개월\n" +
            "· 보존 항목: 세법이 규정하는 모든 거래에 관한 장부 및 증빙서류\n" +
            "· 근거 법령: 국세기본법\n" +
            "· 보존 기간: 5년\n" +
            "· 보존 항목: 전자금융 거래에 관한 기록\n" +
            "· 근거 법령: 전자금융거래법\n" +
            "· 보존 기간: 5년\n" +
            "· 보존 항목: 서비스 방문기록\n" +
            "· 근거 법령: 통신비밀보호법\n" +
            "· 보존 기간: 3개월\n" +
            "  단, 아래와 같이 보존할 필요가 있는 경우 일정 기간 동안 보존합니다.\n" +
            "· 부정이용기록\n" +
            " \n" +
            " \n5. 파기방법\n" +
            "회원탈퇴를 요청하거나 개인정보의 수집 및 이용에 대한 동의를 철회하는 경우 그리고 이용목적이 달성된 여러분의 개인정보는 지체 없이 파기합니다. 종이에 출력된 개인정보는 분쇄기로 분쇄하거나 소각을 통하여 파기하고, 전자적 파일 형태로 저장된 개인정보는 기록을 재생할 수 없는 기술적 방법을 사용하여 삭제합니다. \n" +
            " \n6. 개인정보 최고책임관리자\n" +
            "여러분의 개인정보 취급방침과 관련된 문의, 불만, 조언과 기타 사항은 개인정보관리책임자 및 담당부서로 연락해 주시기 바랍니다.\n" +
            "· 이름:\n" +
            "· 전화:\n" +
            "· 직위:\n" +
            " \n7. 개인정보 취급방침이 변경되는 경우 별도로 알려 드리겠습니다.\n" +
            "개인정보와 관련된 법령의 개정 및 지침 변경이 있을 수 있습니다. 만일 본사의 개인정보 방침에 변경사항이 있을 경우 서비스 내 공지사항 또는 전자우편을 통해 공지하도록 하겠습니다. 변경된 개인정보 취급방침은 게시한 날로부터 7일 후부터 효력이 발생합니다. 하지만, 피치 못하게 여러분의 권리에 중요한 변경이 있을 경우 변경될 내용을 30일 전에 미리 알려드리겠습니다.\n" +
            "· 공고일자: \n" +
            "· 시행일자: ";

    final static String use_ok = "피드피아 서비스<배송기사회원 이용약관>\n" +
            "\n제1조 (목적)\n" +
            "이 약관은 ㈜피드피아(이하 회사라 함)가 운영하는 피드피아 배송기사App에 배송기사회원으로 가입하여 회사가 제공하는 전자상거래 관련 서비스 및 기타 서비스(이하 서비스라 함)를 이용하는 자(이하 배송기사회원이라 함) 간의 권리, 의무 및 제반절차를 확정하고 이를 이행함으로써 상호 발전을 도모하는 것을 그 목적으로 합니다.\n" +
            "\n제2조 (정의)\n" +
            "본 약관에서 사용되는 용어의 정의는 본 약관에서 별도로 규정하는 경우를 제외하고 피드피아 구매자회원 이용약관(이하 구매회원이용약관이라 함) 제2조를 따릅니다.\n" +
            "\n제3조 (약관의 게시 및 개정)\n" +
            "① 회사는 이 약관의 내용을 배송회원이 쉽게 알 수 있도록 피드피아 배송기사App 초기화면 또는 연결화면을 통하여 게시합니다.\n" +
            "② 회사는 필요한 경우 관련 법령을 위배하지 않는 범위 내에서 이 약관을 개정할 수 있으며, 이 경우 개정내용과 적용일자를 명시하여 서비스 초기화면 또는 판매회사에 서문전달 등을 통해 그 적용일자 7일 전부터 적용일자 전일까지 공지합니다. 다만, 변경 내용이 배송기사회원에게 불리한 변경의 경우에는 개정약관의 적용일자 30일 전부터 적용일자까지 공지합니다.\n" +
            "③ 배송기사회원이 개정약관에 동의하지 않는 경우에는 개정 약관의 적용일 이전에 거부 의사를 표시하고 이 약관에 의한 이용계약을 해지할 수 있습니다.\n" +
            "④ 회사가 본 조 제2항에 따라 개정약관을 공지 또는 통지하면서 배송기사회원에게 적용일 전까지 의사표시를 하지 않으면 의사표시가 표명된 것으로 본다는 뜻을 명확하게 공지 또는 통지하였음에도 배송기사회원이 명시적으로 거부의사를 표명하지 아니한 경우 개정약관에 동의한 것으로 봅니다.\n" +
            "\n제4조 (약관의 효력)\n" +
            "① 회사는 이 약관에 규정되지 않은 세부적인 내용에 대해 개별 운영 정책 등(이하 운영정책이라 함)을 제정하여 운영할 수 있으며, 해당 내용을 공지사항을 통하여 게시합니다. 운영정책은 이 약관과 더불어 피드피아 배송기사 서비스 이용계약(이하 이용계약이라 함)의 일부를 구성합니다.\n" +
            "② 회사는 피드피아 배송기사 서비스 중 특정 서비스에 관한 약관(이하 개별약관이라 함)을 별도로 제정할 수 있으며, 배송기사회원이 개별약관에 동의한 경우 개별약관은 이용계약의 일부를 구성하고 개별약관에 이 약관과 상충하는 내용이 있을 경우 개별 약관이 우선적으로 적용됩니다.\n" +
            "③ 본 약관에 의해 배송기사회원으로 가입하고자 하는 자는 배송기사회원약관의 내용을 숙지하고 배송기사회원과 회사간의 권리∙의무관계에 대해 동의함을 확인합니다.\n" +
            "\n제5조 (이용계약의 성립)\n" +
            "① 이용계약은 판매회사로부터 배송계정을 부여 받은 자가 이 약관에 동의함으로써 성립합니다. \n" +
            "\n제 6조 (서비스 내용)\n" +
            "회사가 배송기사App에서 제공하는 서비스는 다음과 같습니다.\n" +
            "1) 사료회사가 배정한 배송목록의 표시\n" +
            "2) 구매자에게 전화\n" +
            "3) 배송지 및 배송사료의 정보 표시\n" +
            "4) 새로운 주문의 배정 알림\n" +
            "상기 서비스 외에 서비스 항목은 추가될 수 있으며, 추가된 서비스에 대해서도 본 이용약관이 동일하게 적용됩니다.\n" +
            "\n제7조 (서비스의 중단)\n" +
            "① 회사는 통제할 수 없는 기술적 장애, 기간통신사업자 등 제3자의 귀책사유 및 천재지변, 국가비상사태 등의 사유로 인하여 서비스제공이 불가능한 경우 서비스 제공을 일시 중단할 수 있습니다.\n" +
            "② 회사는 판매회원의 서비스 이용이 다음 각 호 중 어느 하나 이상에 해당하는 경우 사전통지 없이 서비스 제공을 중단하거나 이용계약을 해제하는 등의 조치를 취할 수 있습니다.\n" +
            "1. 이 약관에서 규정하는 회원의 의무사항을 위반하는 행위를 하는 경우\n" +
            "2. 수사기관으로부터 수사목적의 요청이 있거나, 방송통신심의위원회, 서울시전자상거래센터 등의 기관으로부터 심의결정 또는 서비스 제한 요청이 있는 경우\n" +
            "3. 판매회원의 서비스 이용이 전체 서비스 시스템 과부하의 원인이 되는 경우\n" +
            "4. 이 약관 또는 회사의 다른 서비스 약관 및 정책 등을 위반하여 회사 또는 다른 회원에게 손해가 발생하거나 발생할 우려가 있다고 판단되는 경우\n" +
            "③ 전항 제1호 또는 제4호의 사유로 서비스 제공을 중단하는 경우, 서비스 제공 중단의 구체적인 기준 등에 대해서는 운영정책에서 정합니다.\n" +
            "⑤ 회사는 본 조에 따라 서비스를 중단하는 경우 이러한 사실을 전자우편 등의 방법으로 공지 또는 통지합니다. 단, 기술적 장애 사유 등의 경우에는 사후에 통지할 수 있습니다.\n" +
            "\n제8조 (회사의 권리와 의무)\n" +
            "① 회사는 이 약관에 따라 지속적이고 안정적인 서비스를 제공하는데 최우선의 노력을 다합니다.\n" +
            "② 회사는 배송기사회원으로부터 제기되는 불편사항 및 서비스의 문제점에 대해 정당하다고 판단되는 경우 우선적으로 해당 문제를 해결하며, 신속한 해결이 곤란한 경우에는 배송기사회원에게 그 사유와 처리절차를 안내합니다.\n" +
            "③ 회사는 배송기사회원이 서비스를 이용하여 발생하는 광고 및 매출효과 등의 정보를 통계자료 작성 및 배송기사회원이 이용하는 회사의 다른 서비스에의 적용 등의 목적으로 활용할 수 있습니다.\n" +
            "④ 회사는 배송기사회원의 서비스 이용과 관련한 자료를 수사기관의 수사목적의 요청 및 기타 공공기관이 관련 법률에 따른 절차를 통해 요청하는 경우 배송기사회원의 동의 없이 해당 기관에 제공할 수 있습니다.\n" +
            "⑤ 회사는 판매회원이 서비스에 등록한 상품 및 그 정보 등이 불법정보에 해당한다고 판단되거나, 방송통신심의위원회 등 관련기관으로부터 요청이 있는 경우 또는 판매회원이 서비스를 이 약관 이 외의 목적으로 사용한다고 판단되는 경우 판매회원에 대한 사전 통보 없이 해당 정보를 삭제할 수 있습니다.\n" +
            "\n제8조 (통지)\n" +
            "① 회사는 이 약관과 관련한 통지 시 배송기사회원이 판매자회원에게 제공한 (휴대)전화번호, 로그인 시 동의 창 등의 수단으로 할 수 있습니다.\n" +
            "② 회사는 배송기사회원 전체에 대한 통지의 경우 7일 이상 초기화면에 게시함으로써 제1항의 통지에 갈음할 수 있습니다. 다만, 배송기사 회원의 서비스 이용과 관련하여 중대한 영향을 미치는 사항에 대하여는 본 조 제1항의 통지 수단 중 2개 이상의 방법으로 통지합니다.\n" +
            "③ 배송기사회원이 전항의 의무를 소홀히 하여 발생한 불이익에 대해서는 보호받지 못합니다.\n" +
            "\n제9조 (배송기사회원의 금지행위)\n" +
            "배송기사회원은 다음 각 호에 해당하는 행위를 하여서는 아니 되며 이를 위반한 경우 회사는 서비스 이용정지 및 이용계약해지 등의 조치를 취할 수 있습니다. 서비스 이용정지의 구체적인 기준 등에 대해서는 운영정책에서 정합니다.\n" +
            "\n1. 일반사항\n" +
            "가. 서비스를 통해 음란정보, 거짓정보 등 유해한 정보를 게재하거나 링크하는 행위\n" +
            "나. 범죄행위와 결부되는 모든 행위 및 기타 관계법령에 위반되는 행위\n" +
            "다. 해킹, 컴퓨터 바이러스 유포, 서버 공격 등으로 타인과 회사에 해가 되는 경우\n" +
            "라. 서비스의 안정적 운영을 방해할 목적으로 다량의 정보를 전송하거나 수신자의 의사에 반하여 광고성 정보를 지속적으로 전송하는 행위\n" +
            "마. 통신판매 이외의 목적으로 서비스를 이용하는 행위\n" +
            "바. 피드피아에서의 판매행위와 직접적인 연관이 없는 이벤트 및 홍보를 위해 서비스를 이용하는 행위\n" +
            "사. 회사 서비스를 방해하거나 장애를 발생시킬 수 있는 모든 행위\n" +
            "아. 이 약관에 따른 권리∙의무를 회사의 사전 서면 동의 없이 타인에게 양도, 증여하거나 이를 담보제공 하는 행위\n" +
            "\n제10조 (취득한 개인정보의 보호)\n" +
            "① 배송기사회원은 서비스의 이용에 따라 취득한 구매회원, 판매회원 등 타인의 개인정보를 이 약관에서 정한 목적 이외의 용도로 사용하거나 제3자에게 제공하는 등 외부에 유출할 수 없으며, 관련 법령 등에 따라 철저히 보호하여야 합니다.\n" +
            "② 회사는 배송 등의 목적으로 배송기사회원에게 공개되어 있는 구매회원의 개인정보를 회사의 정책에 따라 상당 기간이 경과한 후 비공개 조치할 수 있습니다.\n" +
            "③ 배송기사회원이 본 조를 위반하여 구매회원, 판매회원 등으로부터 분쟁이 발생하는 경우 자신의 노력과 비용으로 면책받아야 하며, 민/형사 상 일체의 법적 책임을 부담하여야 합니다.\n" +
            "\n제11조 (상품의 배송)\n" +
            "① 배송회원은 배정받은 주문의 배송요청일로부터 1 영업일 이내로 도착예정일을 설정하고, 도착예정시간의 3시간 내로 상품의 발송을 완료하여야 합니다. 또한 배송기사App에 구매자회원이 사인을 등록하게 함으로서 배송이 완료되었음을 증명하여야 합니다. 단, 천재지변 등 불가항력적인 사유가 발생한 경우, 사료회사는 배송일에 대한 준수에 대한 면책권한을 가질 수 있습니다.\n" +
            "\n제12조 (이용계약의 종료)\n" +
            "① 회사는 다음 각호의 사유가 발생한 경우 제 5조에 따라 체결된 이용계약을 해지할 수 있습니다.\n" +
            "1. 본 약관 및 회사의 운영정책을 위반하거나 회사로부터 그 시정을 요구 받은 후 7일 이내에 이를 시정하지 아니한 경우\n" +
            "2. 이용계약의 이행이 불가능한 경우\n" +
            "3. 관련 법령을 위반하거나 배송기사회원의 책임 있는 사유로 인하여 회사가 명예 실추 등 유/무형적 손해를 입은 경우\n" +
            "4. 기타 회사가 판단한 합리적인 사유에 의거하여 이용계약의 해지가 필요하다고 인정 할 경우\n" +
            "② 배송기사회원이 약관에 의한 이용계약을 해지하고자 하는 경우, 소속된 사료회사에 직접 회원 탈퇴를 요청하여야 합니다. 만약 현재 진행 중인 거래, 문의, 또는 민원이 있는 경우에는 탈퇴 신청이 불가능하며, 해당 사항을 처리 완료한 후 탈퇴 및 이용계약 해지가 가능합니다.\n" +
            "③ 이용계약의 해지에도 불구하고 배송회원은 해지 시까지 완결되지 않은 주문건의 배송, 교환, 환불에 필요한 조치를 취하여야 하며, 해지 이전에 이미 판매한 상품과 관련하여 발생한 배송회원의 책임과 관련된 조항은 그 효력을 유지합니다.\n" +
            "④ 본 조에 의한 이용계약의 해지는 발생한 양당사자의 권리관계 및 손해배상 청구권에 영향을 미치지 아니합니다.\n" +
            "\n제13조 (비밀유지)\n" +
            "① 배송시가회원은 구매회원정보 등 서비스 이용과 관련하여 취득한 일체의 정보를 회사의 서면 동의 없이 외부에 유출하거나 이 약관 이 외의 목적으로 사용할 수 없습니다.\n" +
            "② 전항의 의무는 이용계약의 종료 후에도 존속합니다.\n" +
            "\n제14조 (양도금지)\n" +
            "① 배송기사회원은 판매회사의 사전 서면 동의 없이 이 약관에 따른 일체의 권리와 의무를 제3자에게 양도하거나 담보의 목적으로 제공할 수 없습니다.\n" +
            "② 회사는 배송기사회원이 본 조를 위반하는 경우 서비스 제공을 거부할 권한을 가지며, 기존 배송기사회원에 대하여 이용계약을 해지할 수 있다.\n" +
            "\n제15조 (손해배상)\n" +
            "회사 또는 배송기사회원의 명백한 귀책 사유로 이 약관을 위반하여 상대방 또는 다른 회원에게 손해를 발생하게 한 때에는 귀책 당사자는 이를 배상할 책임을 부담합니다.\n" +
            "\n제16조 (회사의 면책)\n" +
            "① 회사는 구매회원과 판매회원간의 통신판매를 위한 거래시스템만을 제공할 뿐, 판매회원이 등록한 상품 및 배송기사 등에 관한 정보 또는 구매회원과의 거래에 관하여 분쟁이 발생한 경우 회사는 그 분쟁에 개입하지 않으며 그 분쟁의 결과로 인한 모든 책임은 판매회원 및 배송회원이 부담합니다. 또한 제3자가 회사를 상대로 민•형사상 등의 문제를 제기하는 경우 판매회원 또는 배송회원은 해당 문제해결을 위해 적극 협조하여야 하며, 이와 관련하여 회사에 손해가 발생한 경우 손해를 배상합니다.\n" +
            "단, 회사는 분쟁의 합리적이고 원활한 조정을 위하여 회사가 설치 운영하는 고객센터를 통하여 예외적으로 당해 분쟁에 개입할 수 있으며, 배송회원은 안전거래센터의 결정을 의칙에 입각하여 최대한 존중해야 합니다.\n" +
            "② 회사는 적법한 권리자의 요구가 있는 경우에는 당해 상품 및 용역 등에 관한 정보를 삭제하거나 수정할 수 있으며, 배송회원은 이로 인한 손해배상을 회사에 청구할 수 없습니다.\n" +
            "③ 회사는 관련법령에 의거하여 배송기사회원의 정보를 열람할 수 있는 방법을 구매회원 및 판매회원에게 제공할 수 있으며, 배송회원은 당해 정보를 기재하지 아니하거나, 허위로 기재함으로써 발생하는 모든 책임을 부담하여야 합니다.\n" +
            "④ 회사는 컴퓨터 등 정보통신설비의 보수, 점검, 교체 및 고장, 통신의 두절 등의 사유가 발생한 경우에는 판매서비스의 제공을 일시적으로 중단할 수 있으며, 이와 관련하여 회사는 고의 또는 중대한 과실이 없는 한 책임을 지지 않습니다.\n" +
            "⑤ 배송기사회원이 자신의 개인정보 또는 배송기사계정의 로그인정보를 타인에게 유출 또는 제공하여 피해가 발생할 경우, 이에 대해서 회사는 책임을 지지 않습니다.\n" +
            "⑥ 기타 관련 법령 및 회사에서 제공한 이용약관 및 개별약관의 변경, 공지사항 등의 확인의무를 게을리하여 발생한 배송기사회원의 피해에 대해서 회사는 책임을 지지 않습니다.\n" +
            "\n제17조 (관할 법원)\n" +
            "이 약관과 회사와 회원 간의 서비스 이용계약 및 회원 상호간의 분쟁에 대해 회사를 당사자로 하는 소송이 제기될 경우에는 회사의 본사 소재지를 관할하는 법원을 합의관할법원으로 정합니다.\n" +
            "\n<부칙>\n" +
            "\n제 1 조 (시행일자)\n" +
            "이 약관은 2017년 04월 01일부터 시행합니다.\n" +
            "\n";

    JSONObject login_data;
    String test = "";
    WebClient webClient;


    Geocoder coder;
    public LocationManager locManager;
    public LocationListener locationListener;
    double latPoint, lngPoint;

    TextView location_target_address;

    boolean location_flag;









    /****** Sign *******/

    DrawingView dv;
    private Paint mPaint;
    private Canvas mCanvas;
    float downx = 0, downy = 0, upx = 0, upy = 0;
    Path path = new Path();


    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    try {
                        if (webClient.recvData != null) {
                            JSONObject obj = new JSONObject(webClient.recvData);
                            if (obj.getBoolean("result")) {

                                setData("SSID", obj.getString("SSID"));
                                setData("UID", obj.getString("UID"));
                                setData("working","1");
                                Intent intent = new Intent(mContext, MainActivity.class);
                                mContext.startActivity(intent);

                            } else {
                                Msg(obj.getString("message"));

                                setData("user_id", "");
                                setData("user_passwd", "");
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case 200:
                    break;
            }
        }
    };


    public User(Context context) {
        mContext = context;
        prefs = mContext.getSharedPreferences("feedpia_delivery", mContext.MODE_PRIVATE);
        editor = prefs.edit();

        try {
            UUID = (String) Build.class.getField("SERIAL").get(null);
        } catch (IllegalAccessException e) {
            UUID = "";
        } catch (NoSuchFieldException e) {
            UUID = "";
        }

        try {
            TelephonyManager manager = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);
            TID = manager.getLine1Number();
            if (TID == null) TID = "";
        } catch (Exception ee) {
            TID = "";
        }

        SSID = getData("SSID");
        location_flag=false;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(12);


        coder = new Geocoder(mContext, Locale.KOREA);
        locManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location locations) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            abstract class LocationResult {
                public abstract void gotLocation(Location location);
            }
        };


    }




    public void setData(String name, String data) {
        editor.putString(name, data);
        editor.commit();
    }

    public String getData(String name) {
        return prefs.getString(name, "");
    }

    public void setBoolean(String name, boolean data) {
        editor.putBoolean(name, data);
        editor.commit();
    }

    public boolean getBoolean(String name) {
        return prefs.getBoolean(name, false);
    }

    boolean InternetCheck() {

        boolean status = false;
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                status = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                status = true;
            }
        } else {
            // not connected to the internet
            status = false;
            Toast.makeText(mContext, "네트워크 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
        }
        return status;
    }


    /*********
     * Default user action
     ***********/

    boolean Login(String user_id, String user_passwd, boolean save_login) {
        boolean status = false;


//        JSONArray json_data;
//        json_data.put

        if (save_login) {
            setData("user_id", user_id);
            setData("user_passwd", user_passwd);
        } else {

        }

        JSONObject obj1 = new JSONObject();
        try {
            obj1.put("USER_ID", user_id);
            obj1.put("USER_PASSWD", user_passwd);
            obj1.put("FCM", getData("FCM"));
            Log.d("FCMS",getData("FCM"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        webClient = new WebClient(URL_DOMAIN + URL_LOGIN, obj1.toString(), mHandler, 100);


        return status;

    }

    /*********
     * location
     *************/


    void setLocationUpdate(){
        location_flag=true;



        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Log.d("[LOC]", "Faield...1");
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }


        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 1, locationListener);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1, locationListener);

    }



    String getLocation(TextView target) {
        location_target_address = target;
        String address = "";

        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Log.d("[LOC]", "Faield...1");
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return null;
        }


        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 1, locationListener);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1, locationListener);

        return address;

    }

    void LocInit() {

        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Log.d("[LOC]", "Faield...1");
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }


        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 1, locationListener);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1, locationListener);

    }

    public void Msg(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    public void DMsg(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }






    /******* Sign part ******/


    void SignView(View v){

        dv = new DrawingView(mContext);
        final PopupWindow popup = new PopupWindow( v,  RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //팝업으로 띄울 커스텀뷰를 설정하고
        View view = inflater.inflate(R.layout.activity_main_sign, null);
        popup.setContentView(view);
//        popup.setAnimationStyle(-1);
        popup.showAtLocation(view, Gravity.CENTER, 0, -100);

//                popup.setBackgroundDrawable(new ColorDrawable(0xb0000000));

        //팝업의 크기 설정
        //  popup.setWindowLayoutMode(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        final LinearLayout sign_pad = (LinearLayout)view.findViewById(R.id.sign_pad);
        final Button sign_save = (Button)view.findViewById(R.id.sign_save);
        final Button sign_cancel = (Button)view.findViewById(R.id.sign_cancel);

        sign_pad.addView(dv);
        //팝업 뷰 터치 되도록
        popup.setTouchable(true);
        //팝업 뷰 포커스도 주고
        popup.setFocusable(true);
        //팝업 뷰 이외에도 터치되게 (터치시 팝업 닫기 위한 코드)
        //           popup.setOutsideTouchable(true);
//                popup.setBackgroundDrawable(new BitmapDrawable());
        //인자로 넘겨준 v 아래로 보여주기
        popup.showAsDropDown(v);

        popup.update();


        sign_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });


    }


    public class DrawingView extends View {

        public int width;
        public int height;
        private Bitmap mBitmap;
        private Path mPath;
        private Paint mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;

        public DrawingView(Context c) {
            super(c);
            context = c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);

        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            canvas.drawPath(mPath, mPaint);
            canvas.drawPath(circlePath, circlePaint);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }

        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }


    long getUnixtime(String date) {
//        String dateString = "Fri, 09 Nov 2012 23:40:18 GMT";
//        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        long unixTime = 0;
        try {
            Date dates = dateFormat.parse(date);

            unixTime = (long) dates.getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return unixTime;
    }

    long getUnixtime(String date, String format) {
//        String dateString = "Fri, 09 Nov 2012 23:40:18 GMT";
//        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z");
        DateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+9"));
        long unixTime = 0;
        try {
            Date dates = dateFormat.parse(date);
            unixTime = (long) dates.getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return unixTime;
    }

    long getUDateNow() {

        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date currentTime = new Date();
        String mTime = mSimpleDateFormat.format(currentTime);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long unixTime = 0;
        try {
            Date dates = dateFormat.parse(mTime);
            unixTime = (long) dates.getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return unixTime;
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }



    public void gpsup(){
        //GPS 업데이트
        final MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
            @Override
            public void gotLocation(Location location) {
                String location_address = "";
                latPoint = location.getLatitude();
                lngPoint = location.getLongitude();

                Log.d("[LOC]:", latPoint + " " + lngPoint + " - " + location_address);

                setData("user_lng",lngPoint+"");
                setData("user_lat",latPoint+"");

                if(true){


                    JSONObject obj1 = new JSONObject();
                    try {

                        obj1.put("SSID", getData("SSID"));
                        obj1.put("UID",getData("UID"));
                        obj1.put("lat",latPoint);
                        obj1.put("lng",lngPoint);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    webClient = new WebClient(URL_DOMAIN + URL_LOCATION_SET, obj1.toString(), mHandler, 200);
                    Log.d("GPS_UP","REFRESH  Lng = "+lngPoint + "  Lat = " + latPoint);
                    //Toast.makeText(mContext, "SERVICE GPS UP  Lng = "+lngPoint + "  Lat = " + latPoint, Toast.LENGTH_LONG).show();
                    Toast.makeText(mContext, "GPS 업데이트", Toast.LENGTH_SHORT).show();

                    location_flag=false;

                }

                try {
                    if (coder != null) {
                        List<Address> address = coder.getFromLocation(latPoint, lngPoint, 1);
                        if (address != null && address.size() > 0) {
                            location_address = address.get(0).getAddressLine(0).toString();
                            if (location_target_address != null) {
                                location_target_address.setText(location_address);
                            } else {
                                //Toast.makeText(mContext, location_address, Toast.LENGTH_SHORT).show();
                            }

                            if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                return;
                            }
                            //    locManager.removeUpdates(locationListener);
                        } else {
                            //  Toast.makeText(mContext, "Address null.", Toast.LENGTH_SHORT).show();

                            if (location_target_address != null) {
                                location_target_address.setText("위치정보를 찾을 수 없습니다.");
                            } else {
                                Toast.makeText(mContext, "위치정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (IOException e) {
                    // order_input_address.setText("");
                    if (location_target_address != null) {
                        location_target_address.setText("위치정보를 찾을 수 없습니다.");
                    } else {
                        Toast.makeText(mContext, "위치정보를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }

                    locManager.removeUpdates(locationListener);
                    Log.d("location: error", e.toString());
                }
            }
        };

        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(mContext, locationResult);
    }


}
