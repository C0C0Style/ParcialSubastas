const express = require('express');
const cors = require('cors');
const fs = require('fs');
const app = express();
const PORT = 3000;

const SUBASTAS_FILE = './subastas.json';
const PUJAS_FILE = './pujas.json';

app.use(cors());
app.use(express.json({ limit: '10mb' }));

let subastas = [];
let pujas = [];

// Cargar subastas desde archivo
try {
    const data = fs.readFileSync(SUBASTAS_FILE, 'utf8');
    subastas = JSON.parse(data);
    console.log(`📁 Cargadas ${subastas.length} subastas desde archivo.`);
} catch (err) {
    console.log('⚠️ No se encontró el archivo subastas.json. Iniciando lista vacía.');
    subastas = [];
}

// Cargar pujas desde archivo
try {
    const data = fs.readFileSync(PUJAS_FILE, 'utf8');
    pujas = JSON.parse(data);
    console.log(`📁 Cargadas ${pujas.length} pujas desde archivo.`);
} catch (err) {
    console.log('⚠️ No se encontró pujas.json. Iniciando lista vacía.');
    pujas = [];
}

// Guardar pujas en archivo
function guardarPujasEnArchivo() {
    fs.writeFileSync(PUJAS_FILE, JSON.stringify(pujas, null, 2));
}

// Guardar subastas en archivo
function guardarSubastasEnArchivo() {
    fs.writeFileSync(SUBASTAS_FILE, JSON.stringify(subastas, null, 2));
    console.log(`💾 Guardando subastas con inscritos actualizados:`);
}

// GET /subastas
app.get('/subastas', (req, res) => {
    res.json(subastas.map(s => ({
        ...s,
        fechaSubasta: s.fechaSubasta || s.fechaInicio
    })));
});

// GET /subastas/:id
app.get('/subastas/:id', (req, res) => {
    const subasta = subastas.find(s => s.id == req.params.id);
    if (!subasta) {
        return res.status(404).json({ error: 'No encontrada' });
    }
    res.json(subasta);
});


// POST /subastas
app.post('/subastas', (req, res) => {
    const nueva = req.body;

    if (!nueva.nombre || !nueva.ofertaMinima || !nueva.fechaSubasta || !nueva.imagen) {
        return res.status(400).json({ error: 'Faltan datos obligatorios' });
    }

    nueva.id = subastas.length + 1;
    nueva.inscritos = 0;
    subastas.push(nueva);

    try {
        guardarSubastasEnArchivo();
        res.status(201).json(nueva);
    } catch (e) {
        res.status(500).json({ error: 'Error al guardar en archivo' });
    }
});

// POST /pujas
app.post('/pujas', (req, res) => {
    // ✅ Volver a cargar pujas antes de procesar
    try {
        const data = fs.readFileSync(PUJAS_FILE, 'utf8');
        pujas = JSON.parse(data);
    } catch (e) {
        console.error('❌ Error al recargar pujas.json:', e);
    }

    const puja = req.body;

    if (!puja.subastaId || !puja.numero || !puja.valor) {
        return res.status(400).json({ error: 'Puja incompleta' });
    }

    const subastaId = Number(puja.subastaId);

    // Verificar si ya existe una puja con ese subastaId y numero
    const indexExistente = pujas.findIndex(p =>
        Number(p.subastaId) === subastaId && Number(p.numero) === Number(puja.numero)
    );

    if (indexExistente !== -1) {
        pujas[indexExistente].valor = puja.valor;
        console.log('🔄 Puja actualizada:', pujas[indexExistente]);
    } else {
        pujas.push(puja);
        console.log('✅ Puja creada:', puja);
    }

    guardarPujasEnArchivo();

    // 👉 Recalcular inscritos únicos para esta subasta
    const numerosUnicos = pujas
        .filter(p => Number(p.subastaId) === subastaId)
        .map(p => Number(p.numero));

    const numerosDistintos = [...new Set(numerosUnicos)];

    const subasta = subastas.find(s => Number(s.id) === subastaId);
    if (subasta) {
        subasta.inscritos = numerosDistintos.length;

        console.log(`📊 Subasta actualizada con ${subasta.inscritos} inscritos:`);

        guardarSubastasEnArchivo();
    } else {
        console.warn(`⚠️ No se encontró subasta con id ${subastaId}`);
    }

    res.status(200).json({ mensaje: 'Puja guardada', puja });
});


app.listen(PORT, '0.0.0.0', () => {
    console.log(`🚀 Servidor escuchando en http://0.0.0.0:${PORT}`);
});

// PUT /pujas/:subastaId/:numero
app.put('/pujas/:subastaId/:numero', (req, res) => {
    const { subastaId, numero } = req.params;
    const nuevaPuja = req.body;

    const index = pujas.findIndex(p => 
        p.subastaId == subastaId && p.numero == numero
    );

    if (index === -1) {
        return res.status(404).json({ error: 'Puja no encontrada' });
    }

    pujas[index] = { ...pujas[index], ...nuevaPuja };
    guardarPujasEnArchivo();
    res.json({ mensaje: 'Puja actualizada', puja: pujas[index] });
});

// GET /pujas/subasta/:subastaId
app.get('/pujas/subasta/:subastaId', (req, res) => {
    const { subastaId } = req.params;
    const pujasSubasta = pujas.filter(p => p.subastaId == subastaId);
    res.json(pujasSubasta);
});

// GET /pujas/:subastaId/:numero
app.get('/pujas/:subastaId/:numero', (req, res) => {
    const { subastaId, numero } = req.params;
    const puja = pujas.find(p => p.subastaId == subastaId && p.numero == numero);

    if (!puja) {
        return res.status(404).json({ error: 'No encontrada' });
    }

    res.json(puja);
});



