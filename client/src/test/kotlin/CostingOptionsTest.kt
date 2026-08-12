import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.valhalla.api.models.AutoCostingOptions
import com.valhalla.api.models.BicycleCostingOptions
import com.valhalla.api.models.CostingOptions
import com.valhalla.api.models.PedestrianCostingOptions
import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.assertions.json.shouldNotContainJsonKey
import kotlin.test.Test

class CostingOptionsTest {

  private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

  @Test
  fun testPedestrianShortest() {
    val options = CostingOptions(
      pedestrian = PedestrianCostingOptions(shortest = true, maxHikingDifficulty = 6, useHills = 1.0)
    )

    val actualJson = moshi.adapter(CostingOptions::class.java).toJson(options)

    actualJson.shouldContainJsonKeyValue("$.pedestrian.shortest", true)
    actualJson.shouldContainJsonKeyValue("$.pedestrian.max_hiking_difficulty", 6)
    actualJson.shouldContainJsonKeyValue("$.pedestrian.use_hills", 1.0)
  }

  @Test
  fun testBicycleShortest() {
    val options = CostingOptions(bicycle = BicycleCostingOptions(shortest = true, useHills = 1.0))

    val actualJson = moshi.adapter(CostingOptions::class.java).toJson(options)

    actualJson.shouldContainJsonKeyValue("$.bicycle.shortest", true)
  }

  @Test
  fun testAutoKeepsShortestThroughBaseOptions() {
    val options = CostingOptions(auto = AutoCostingOptions(shortest = true))

    val actualJson = moshi.adapter(CostingOptions::class.java).toJson(options)

    actualJson.shouldContainJsonKeyValue("$.auto.shortest", true)
  }

  @Test
  fun testAutoAccessPenalties() {
    val options = CostingOptions(
      auto = AutoCostingOptions(
        useTracks = 1.0,
        gatePenalty = 0,
        gateCost = 0,
        privateAccessPenalty = 0,
        destinationOnlyPenalty = 0
      )
    )

    val actualJson = moshi.adapter(CostingOptions::class.java).toJson(options)

    actualJson.shouldContainJsonKeyValue("$.auto.use_tracks", 1.0)
    actualJson.shouldContainJsonKeyValue("$.auto.private_access_penalty", 0)
    actualJson.shouldContainJsonKeyValue("$.auto.destination_only_penalty", 0)
  }

  @Test
  fun testPedestrianAccessPenalties() {
    val options = CostingOptions(
      pedestrian = PedestrianCostingOptions(privateAccessPenalty = 0, destinationOnlyPenalty = 0)
    )

    val actualJson = moshi.adapter(CostingOptions::class.java).toJson(options)

    actualJson.shouldContainJsonKeyValue("$.pedestrian.private_access_penalty", 0)
    actualJson.shouldContainJsonKeyValue("$.pedestrian.destination_only_penalty", 0)
  }

  @Test
  fun testUnsetPenaltiesAreOmitted() {
    val options = CostingOptions(pedestrian = PedestrianCostingOptions(shortest = true))

    val actualJson = moshi.adapter(CostingOptions::class.java).toJson(options)

    actualJson.shouldNotContainJsonKey("$.pedestrian.private_access_penalty")
    actualJson.shouldNotContainJsonKey("$.pedestrian.destination_only_penalty")
  }
}
