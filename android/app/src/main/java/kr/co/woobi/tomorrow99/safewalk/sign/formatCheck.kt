package kr.co.woobi.tomorrow99.safewalk.sign

/****************************************
 * Name:            checkPWformet
 * description:     PW 형식 확인
 *
 * Author: Jeong MinGye
 * Create: 20.07.27.
 * Update:
 **************************************/
fun checkPWformet(content: String): Boolean {
    //todo 비밀번호 유효성 검사 버그 고칠것
    val reg1 = Regex("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@!%*#?&])[A-Za-z0-9$@!%*#?&]{8,}$")
    return true
    if(!content.matches(reg1)) return false
}

/****************************************
 * Name:            isEmail
 * description:     email 인지 확인
 *
 * Author: Jeong MinGye
 * Create: 20.07.27.
 * Update:
 **************************************/
fun isEmail(content: String): Boolean {
    val reg = Regex("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$")
    if(!content.matches(reg)) return false

    return true
}

/****************************************
 * Name:            checkPhoneNumber
 * description:     email 인지 확인
 *
 * Author: Jeong MinGye
 * Create: 20.07.27.
 * Update:
 **************************************/
fun checkPhoneNumber(content: String): Boolean {
    val reg = Regex("01[016789][0-9]{7,8}$")
    if(!content.matches(reg)) return false
    return true
}

/****************************************
 * Name:            checkPhoneNumber
 * description:     email 인지 확인
 *
 * Author: Jeong MinGye
 * Create: 20.07.27.
 * Update:
 **************************************/
fun checkNickName(content: String): Boolean {
    val reg = Regex("([ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]{4,15})$")
    if(!content.matches(reg)) return false
    return true
}