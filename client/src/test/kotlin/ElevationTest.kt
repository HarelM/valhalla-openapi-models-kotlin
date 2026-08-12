import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.valhalla.api.models.CostingModel
import com.valhalla.api.models.MapMatchCostingModel
import com.valhalla.api.models.MapMatchRequest
import com.valhalla.api.models.RouteLeg
import com.valhalla.api.models.RouteRequest
import com.valhalla.api.models.RoutingWaypoint
import io.kotest.assertions.json.shouldContainJsonKeyValue
import io.kotest.assertions.json.shouldNotContainJsonKey
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ElevationTest {

  private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

  @Test
  fun testRouteRequestWithElevationInterval() {
    val request = RouteRequest(
      locations =
      listOf(
        RoutingWaypoint(lat = 45.843812, lon = -123.768205),
        RoutingWaypoint(lat = 45.869701, lon = -123.766121)
      ),
      costing = CostingModel.auto,
      elevationInterval = 30.0
    )

    val actualJson = moshi.adapter(RouteRequest::class.java).toJson(request)

    actualJson.shouldContainJsonKeyValue("$.elevation_interval", 30.0)
  }

  @Test
  fun testRouteRequestWithoutElevationIntervalOmitsIt() {
    val request = RouteRequest(
      locations =
      listOf(
        RoutingWaypoint(lat = 45.843812, lon = -123.768205),
        RoutingWaypoint(lat = 45.869701, lon = -123.766121)
      ),
      costing = CostingModel.auto
    )

    val actualJson = moshi.adapter(RouteRequest::class.java).toJson(request)

    actualJson.shouldNotContainJsonKey("$.elevation_interval")
  }

  @Test
  fun testMapMatchRequestWithElevationInterval() {
    val request = MapMatchRequest(
      costing = MapMatchCostingModel.pedestrian,
      encodedPolyline = "_grbgAh~{nhF?lBAzBFvB",
      elevationInterval = 30.0
    )

    val actualJson = moshi.adapter(MapMatchRequest::class.java).toJson(request)

    actualJson.shouldContainJsonKeyValue("$.elevation_interval", 30.0)
  }

  @Test
  fun testRouteLegWithElevation() {
    val json =
      """
      {
        "maneuvers": [],
        "shape": "_grbgAh~{nhF?lBAzBFvB",
        "elevation_interval": 30.0,
        "elevation": [329.1, 331.4, 334.0],
        "summary": {
          "time": 120.0,
          "length": 1.2,
          "min_lat": 45.843812,
          "max_lat": 45.869701,
          "min_lon": -123.768205,
          "max_lon": -123.766121
        }
      }
      """

    val leg = moshi.adapter(RouteLeg::class.java).fromJson(json)

    leg?.elevationInterval shouldBe 30.0
    leg?.elevation shouldBe listOf(329.1, 331.4, 334.0)
  }

  @Test
  fun testRouteLegWithoutElevation() {
    val json =
      """
      {
        "maneuvers": [],
        "shape": "_grbgAh~{nhF?lBAzBFvB",
        "summary": {
          "time": 120.0,
          "length": 1.2,
          "min_lat": 45.843812,
          "max_lat": 45.869701,
          "min_lon": -123.768205,
          "max_lon": -123.766121
        }
      }
      """

    val leg = moshi.adapter(RouteLeg::class.java).fromJson(json)

    leg?.elevationInterval shouldBe null
    leg?.elevation shouldBe null
  }
}
