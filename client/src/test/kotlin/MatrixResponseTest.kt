import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.valhalla.api.models.Coordinate
import com.valhalla.api.models.MatrixResponse
import kotlin.test.Test
import kotlin.test.assertEquals

class MatrixResponseTest {

  private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

  /**
   * `sources`/`targets` come back as one location per source/target, in request order - not
   * nested per row like `sources_to_targets`. A response modeled with the nested shape fails to
   * decode Valhalla's actual output with a Moshi "Expected BEGIN_ARRAY but was BEGIN_OBJECT"
   * error, which is how this was found: `valhalla-mobile`'s new `matrix()` wrapper threw it
   * against a real engine response.
   */
  @Test
  fun testMatrixResponseDecodesFlatSourcesAndTargets() {
    val json =
        """
        {
          "sources": [{"lat":40.744014,"lon":-73.990508},{"lat":40.739735,"lon":-73.979713}],
          "targets": [{"lat":40.752522,"lon":-73.985015}],
          "sources_to_targets": [[{"distance":1.2,"time":300,"from_index":0,"to_index":0}],
                                  [{"distance":0.0,"time":0,"from_index":1,"to_index":0}]],
          "units": "kilometers"
        }
        """
            .trimIndent()

    val response = moshi.adapter(MatrixResponse::class.java).fromJson(json)!!

    assertEquals(
        listOf(Coordinate(lat = 40.744014, lon = -73.990508), Coordinate(lat = 40.739735, lon = -73.979713)),
        response.sources)
    assertEquals(listOf(Coordinate(lat = 40.752522, lon = -73.985015)), response.targets)
    assertEquals(1.2, response.sourcesToTargets[0][0].distance)
    assertEquals(0.0, response.sourcesToTargets[1][0].distance)
  }
}
