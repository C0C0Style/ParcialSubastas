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
}

// GET /subastas
app.get('/subastas', (req, res) => {
    res.json(subastas.map(s => ({
        ...s,
        fechaSubasta: s.fechaSubasta || s.fechaInicio
    })));
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
    const puja = req.body;

    if (!puja.subastaId || !puja.numero || !puja.valor) {
        return res.status(400).json({ error: 'Puja incompleta' });
    }

    pujas.push(puja);
    guardarPujasEnArchivo();
    console.log('✅ Puja recibida:', puja);

    res.status(201).json({ mensaje: 'Puja recibida', puja });
});

app.listen(PORT, '0.0.0.0', () => {
    console.log(`🚀 Servidor escuchando en http://0.0.0.0:${PORT}`);
});
