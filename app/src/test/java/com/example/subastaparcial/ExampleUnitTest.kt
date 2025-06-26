package com.example.subastaparcial


import org.junit.Assert.*
import com.example.subastaparcial.data.model.Subasta
import com.example.subastaparcial.data.network.SubastaApiService
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SubastaApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: SubastaApiService

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/")) // Usa el servidor de pruebas
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SubastaApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `obtenerSubastas devuelve lista correctamente`() = runBlocking {
        // Datos simulados
        val subastasSimuladas = listOf(
            Subasta(1, "Subasta 1", 1000.0, "2025-07-01", "imagen1.jpg", 3),
            Subasta(2, "Subasta 2", 2000.0, "2025-08-01", "imagen2.jpg", 5)
        )
        val respuestaJson = Gson().toJson(subastasSimuladas)

        // Preparar respuesta mock
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(respuestaJson)
        )

        // Ejecutar API
        val resultado = api.obtenerSubastas()

        // Validar
        assertEquals(2, resultado.size)
        assertEquals("Subasta 1", resultado[0].nombre)
        assertEquals(2000.0, resultado[1].ofertaMinima, 0.01)
    }
}