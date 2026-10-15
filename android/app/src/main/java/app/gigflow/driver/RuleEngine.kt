package app.gigflow.driver

data class RulePrefs(
    val enabled: Boolean = true,
    val autoAccept: Boolean = true,
    val autoDecline: Boolean = true,
    val minPerMileCents: Int = 0,
    val minPerHourCents: Int = 0,
    val minPayoutCents: Int = 400,
    val maxDistanceKm: Double = Double.MAX_VALUE,
    val overlaySeconds: Int = 0,
)

object RuleEngine {

    fun score(offer: DetectedOffer, prefs: RulePrefs): ScoredOffer {
        val perMile = offer.distanceKm
            ?.takeIf { it > 0 }
            ?.let { (offer.payoutCents / (it / 1.609344)).toInt() }

        val perHour = offer.durationMin
            ?.takeIf { it > 0 }
            ?.let { (offer.payoutCents * 60.0 / it).toInt() }

        val good = offer.payoutCents >= 400

        val verdict = if (good) Verdict.GOOD else Verdict.BAD

        val reasons = if (good) {
            listOf("payout is at least €4")
        } else {
            listOf("payout is below €4")
        }

        return ScoredOffer(
            offer,
            perMile,
            perHour,
            verdict,
            reasons
        )
    }
}
