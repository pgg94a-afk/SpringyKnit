package com.springyknit.app.util

import kotlin.random.Random

object ProjectNameGenerator {

    private val adjectives = listOf(
        "포근한", "게으른", "알록달록한", "부드러운", "몽글몽글한",
        "까슬까슬한", "폭신한", "쫀쫀한", "헐렁한", "엉킨",
        "따뜻한", "시원한", "은은한", "화려한", "소박한",
        "빈티지한", "나른한", "부지런한", "행복한", "끈질긴",
        "설레는", "평화로운", "꼼꼼한", "시크한", "우아한",
        "고독한", "치밀한", "수상한", "다정한", "욕심많은"
    )

    private val nouns = listOf(
        "UFO", "수세미", "뜨린이", "실파먹기", "장비빨",
        "실지옥", "수집가", "신데렐라", "푸르시오", "문어발",
        "뜨태기", "봉다리", "함뜨", "득템", "착샷",
        "모헤어", "자투리", "숏팁", "단수링", "보빈",
        "콘사", "합사", "블로킹", "프로젝트백", "스와치",
        "줄바늘", "돗바늘", "와인더", "실타래", "곰손"
    )

    fun generate(): String {
        val adjective = adjectives[Random.nextInt(adjectives.size)]
        val noun = nouns[Random.nextInt(nouns.size)]
        return "$adjective $noun"
    }
}
