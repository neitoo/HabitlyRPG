package ru.neito.habitlyrpg.Model

data class DataShop(var id: Int,
                    var image: String,
                    var price: Int,
                    var damage: Int,
                    var name: String,
                    var description: String,
                    var atLvl: Int)