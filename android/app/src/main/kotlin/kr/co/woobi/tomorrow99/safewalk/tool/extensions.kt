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

fun calDistance(
    lat1: Double,
    lon1: Double,
    lat2: Double,
    lon2: Double
): Double {
    val theta: Double
    var dist: Double
    theta = lon1 - lon2
    dist =
        Math.sin(lat1.toRadian()) * Math.sin(
            lat2.toRadian()
        ) + (Math.cos(
            lat1.toRadian()
        )
                * Math.cos(lat2.toRadian()) * Math.cos(
            theta.toRadian()
        ))
    dist = Math.acos(dist)
    dist = dist.toDegree()
    dist = dist * 60 * 1.1515
    dist = dist * 1.609344 // 단위 mile 에서 km 변환.
    dist = dist * 1000.0 // 단위  km 에서 m 로 변환
    return dist
}