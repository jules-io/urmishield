package com.urmilabs.shield.ai

class TrustScoreManager {
    fun calculateScore(deepfakeProbability: Float, intentProbability: Float): Float {
        // Simple linear combination
        val deepfakeWeight = 0.4f
        val intentWeight = 0.6f
        
        return (deepfakeProbability * deepfakeWeight) + (intentProbability * intentWeight)
    }

    fun getRiskLevel(score: Float): RiskLevel {
        return when {
            score >= 0.7f -> RiskLevel.HIGH
            score >= 0.3f -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }
    }

    enum class RiskLevel {
        HIGH, MEDIUM, LOW
    }
}
