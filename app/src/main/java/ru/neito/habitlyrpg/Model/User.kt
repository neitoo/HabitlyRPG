package ru.neito.habitlyrpg.Model

class User(_id: String?, _username: String, _email: String?, _HP: Int?, _experience: Int?, _Lvl: Int?,_money: Int?) {
    var id: String? = _id
    var username: String = _username
    var email: String? = _email
    var HP: Int ?= _HP
    var experience: Int ?= _experience
    var Lvl: Int ?= _Lvl
    var money: Int? = _money

}