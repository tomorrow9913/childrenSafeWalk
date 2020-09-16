package kr.co.woobi.tomorrow99.safewalk.tool


/**
 * Created by SungBin on 2020-09-14.
 */

// todo: 비밀번호 유호성 검사 고치기
fun String.isPassword() = this.matches(Regex("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@!%*#?&])[A-Za-z0-9$@!%*#?&]{8,}$"))

fun String.isEmail() =
    this.matches(Regex("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*\\.[a-zA-Z]{2,3}$"))

fun String.isPhoneNumber() = this.matches(Regex("01[016789][0-9]{7,8}$"))

fun String.isNickName() = this.matches(Regex("([ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]{4,15})$"))

fun Double.toRadian() = this * Math.PI / 180

fun Double.toDegree() = this * 180 / Math.PI

fun Double.toKilometer() = this * 1.609344

fun Double.toMeter() = this * 1000