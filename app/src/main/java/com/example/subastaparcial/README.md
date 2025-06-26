📱 Arquitectura de la Aplicación de Subastas
Este documento describe la estructura y funcionamiento de una aplicación móvil de subastas con backend en Node.js y frontend en Android (Jetpack Compose + MVVM). Se detallan las carpetas, la relación entre ellas, los endpoints disponibles en el backend y su lógica específica.

📁 Estructura del Proyecto Android
🧠 data/
Contiene los modelos de datos, lógica de red y acceso a los datos persistentes.

model/:
Define las entidades principales:

Subasta.kt: Clase que representa una subasta.

Puja.kt: Clase que representa una puja.

network/:
Maneja la conexión con el backend.

ApiService.kt: Interfaz con las rutas HTTP (usando Retrofit).

RetrofitInstance.kt: Configura la instancia de Retrofit.

repository/:
Abstrae el acceso a los datos y coordina entre network y viewmodel.

SubastaRepository.kt

PujaRepository.kt

🎨 ui/
Contiene las pantallas y componentes visuales de la app, organizadas por funcionalidades:

subastaslist/:

Pantalla que muestra todas las subastas disponibles (SubastasListScreen.kt).

crearsubasta/:

Formulario para crear una nueva subasta (CrearSubastaScreen.kt).

subastadetalle/:

Detalle de una subasta específica y listado de sus pujas (SubastaDetalleScreen.kt).

🧩 viewmodel/
ViewModels que gestionan el estado de las pantallas:

SubastasListViewModel.kt

CrearSubastaViewModel.kt

SubastaDetalleViewModel.kt

🚪 MainActivity.kt
Punto de entrada de la aplicación. Se encarga de:

Configurar la navegación entre pantallas.

Iniciar la app con la pantalla de lista de subastas.

🌐 Backend API – Node.js + Express
El backend actúa como proveedor de datos para la app móvil, utilizando archivos .json como sistema de persistencia local.

📂 Estructura del Backend (/backend)
server.js: Servidor principal con todas las rutas configuradas.

subastas.json: Almacena las subastas existentes.

pujas.json: Almacena todas las pujas realizadas.

📡 API REST – Rutas Disponibles
📘 Subastas
Método	Ruta	Descripción
GET	/subastas	Retorna todas las subastas registradas
GET	/subastas/:id	Detalle de una subasta específica
POST	/subastas	Crea una nueva subasta

💰 Pujas
Método	Ruta	Descripción
POST	/pujas	Crea o actualiza una puja (evita duplicados)
PUT	/pujas/:subastaId/:numero	Modifica el valor de una puja específica
GET	/pujas/:subastaId/:numero	Obtiene una puja por ID de subasta y número de participante
GET	/pujas/subasta/:subastaId	Devuelve todas las pujas de una subasta
DELETE	/pujas/:subastaId/:numero	Elimina una puja específica

🧠 Lógica Especial del Backend
✅ Persistencia local:
Los datos se guardan en archivos .json, permitiendo conservar información entre reinicios del servidor.

🔁 Recuento automático de participantes únicos:
Cada vez que se crea, modifica o elimina una puja, se recalcula el número total de participantes únicos (numero) en esa subasta.

🧹 Prevención de duplicados:
Si un participante ya pujó por una subasta, su puja se actualiza, no se duplica.

🔄 Carga inicial de datos:
Al arrancar, el servidor carga el contenido de los archivos .json, si existen.

🧪 Validación básica de datos:
El backend valida que existan los campos obligatorios (por ejemplo: subastaId, valor, numero) en cada petición.

🚀 Instrucciones de Ejecución
🔧 Requisitos Previos
Tener instalado:

Node.js

npm

📦 Instalar dependencias
bash
Copy
Edit
cd backend
npm install express cors fs
▶️ Ejecutar el servidor
bash
Copy
Edit
node server.js
El servidor quedará escuchando por defecto en http://localhost:3000.

🛠 Funcionalidades futuras (opcional)
Aquí algunos ejemplos que se podrían implementar:

📅 Cuenta regresiva de la subasta: agregar un campo de tiempo límite para aceptar pujas.

📈 Historial de pujas por usuario: para ver quién ha pujado y cuánto.

🔐 Autenticación básica: por nombre o email del usuario.

🔄 Socket en tiempo real: para recibir actualizaciones en la app sin refrescar.

🧪 Test unitarios y de integración: para asegurar la calidad del código backend.