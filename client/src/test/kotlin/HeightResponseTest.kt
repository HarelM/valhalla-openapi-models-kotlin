import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.valhalla.api.models.HeightResponse
import kotlin.test.Test
import kotlin.test.assertEquals

class HeightResponseTest {

  private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

  @Test
  fun testHeightResponseDecodesNullAndDecimalHeights() {
    val json = """{"height":[100.5,null,37],"range_height":[[0,100.5],[50.2,null]]}"""

    val response = moshi.adapter(HeightResponse::class.java).fromJson(json)!!

    assertEquals(listOf(100.5, null, 37.0), response.height)
    assertEquals(listOf(listOf(0.0, 100.5), listOf(50.2, null)), response.rangeHeight)
  }
}
