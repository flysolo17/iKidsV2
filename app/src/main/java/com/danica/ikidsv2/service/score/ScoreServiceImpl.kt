package com.danica.ikidsv2.service.score

import com.danica.ikidsv2.models.*
import com.danica.ikidsv2.utils.Constants
import com.danica.ikidsv2.utils.UiState
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.WriteBatch

class ScoreServiceImpl(val firestore: FirebaseFirestore) : ScoreService {

    override fun addScore(score: Score, result: (UiState<String>) -> Unit) {
        score.id = score.lessonID!! + score.studentID
        result.invoke(UiState.Loading)
        firestore.collection(Constants.SCORES_TABLE)
            .document(score.id!!)
            .set(score)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.Successful("Score Saved!"))
                } else {
                    result.invoke(UiState.Failed("Failed saving score.."))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }

    override fun getScore(studentID: List<String>, result: (UiState<List<Leaderboard>>) -> Unit) {
        result.invoke(UiState.Loading)
        val leaderboardList = mutableListOf<Leaderboard>()
        studentID.map { id ->
            val q = firestore.collection(Constants.SCORES_TABLE).whereEqualTo("studentID",id)
            q.get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val scores = mutableListOf<Score>()
                        for (doc in task.result) {
                            val data = doc.toObject(Score::class.java)
                            scores.add(data)
                        }
                        leaderboardList.add(Leaderboard(id,scores.sumOf { s ->s.score!! }))
                    }
                }
        }
    }

    override fun getLeaderBoard(result: (UiState<List<TopScores>>) -> Unit) {
        val topScore = mutableListOf<TopScores>()
        result.invoke(UiState.Loading)
        firestore.collection(Constants.USER_TABLE)
            .whereEqualTo("type",UserType.LEARNER)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    topScore.clear()
                    for(snapshot in it.result) {
                        val student = snapshot.toObject(User::class.java)
                        val q = firestore.collection(Constants.SCORES_TABLE)
                            .whereEqualTo("studentID", snapshot.id)
                        q.get()
                            .addOnCompleteListener { it2 ->
                                if (it2.isSuccessful) {
                                    val scores = mutableListOf<Score>()
                                    for (snap in it2.result) {
                                        val score = snap.toObject(Score::class.java)
                                        scores.add(score)
                                    }
                                    topScore.add(
                                        TopScores(
                                            student.avatar,
                                            student.name,
                                            scores.sumOf { s -> s.score!! })
                                    )
                                }
                            }
                    }
                    result.invoke(UiState.Successful(topScore))
                } else {
                    result.invoke(UiState.Failed("Failed getting scores"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }

    override fun getAllScore(result: (UiState<List<Score>>) -> Unit) {
        val scoreList = mutableListOf<Score>()
        result.invoke(UiState.Loading)
        firestore.collection(Constants.SCORES_TABLE)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    for (snap in it.result) {
                        val score = snap.toObject(Score::class.java)
                        scoreList.add(score)
                    }
                    result.invoke(UiState.Successful(scoreList))
                } else {
                    result.invoke(UiState.Failed("Failed getting scores"))
                }

            }.addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }
}