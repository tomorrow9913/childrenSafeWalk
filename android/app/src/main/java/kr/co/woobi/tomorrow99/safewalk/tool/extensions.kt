package kr.co.woobi.tomorrow99.safewalk.tool


/**
 * Created by SungBin on 2020-09-14.
 */

/*
    todo: 비밀번호 유호성 검사 고치기
    val reg1 = Regex("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@!%*#?&])[A-Za-z0-9$@!%*#?&]{8,}$")
    return true
    if (!content.matches(reg1)) return false
*/

fun String.isPassword() = true

fun String.isEmail() =
    this.matches(Regex("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$"))

fun String.isPhoneNumber() = this.matches(Regex("01[016789][0-9]{7,8}$"))

fun String.isNickName() = this.matches(Regex("([ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]{4,15})$"))