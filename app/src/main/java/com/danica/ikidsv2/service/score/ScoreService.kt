package com.danica.ikidsv2.service.score

import com.danica.ikidsv2.models.Leaderboard
import com.danica.ikidsv2.models.Score
import com.danica.ikidsv2.models.TopScores
import com.danica.ikidsv2.models.User
import com.danica.ikidsv2.utils.UiState

interface ScoreService {
    fun addScore(score : Score,result : (UiState<String>) -> Unit)
    fun getScore(studentID : List<String>,result: (UiState<List<Leaderboard>>) -> Unit)
    fun getLeaderBoard(result: (UiState<List<TopScores>>) -> Unit)
    fun getAllScore(result: (UiState<List<Score>>) -> Unit)
}