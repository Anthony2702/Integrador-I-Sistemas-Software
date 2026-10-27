"""Genera los 5 procesos BPMN 2.0 (con diagrama) del sistema AUTODEV.

Los .bpmn se abren en Bizagi Modeler, Camunda Modeler o https://demo.bpmn.io
Uso: python3 generar_bpmn.py
"""
from xml.sax.saxutils import escape
from pathlib import Path

ANCHO_COL = 165
TAM = {"task": (120, 70), "start": (36, 36), "timer": (36, 36), "end": (36, 36), "xor": (50, 50)}

PROCESOS = [
    {
        "id": "P1", "archivo": "P1_Registro_de_venta.bpmn", "nombre": "P1 Registro de venta de licencia",
        "carriles": ["Cliente", "Vendedor AUTODEV", "Sistema AUTODEV"],
        "nodos": [
            ("s1", "start", 0, 0, 0, "Solicita licencia de un programa"),
            ("t1", "task", 0, 1, 0, "Paga por Yape, Plin o transferencia"),
            ("t2", "task", 1, 2, 0, "Verifica el pago recibido"),
            ("g1", "xor", 1, 3, 0, "¿Cliente registrado?"),
            ("t3", "task", 1, 4, 1, "Registra al cliente (P4)"),
            ("t4", "task", 1, 5, 0, "Registra la venta en el sistema"),
            ("t5", "task", 2, 6, 0, "Valida datos y calcula vencimiento (+1 año)"),
            ("t6", "task", 2, 7, 0, "Guarda la licencia como Vigente"),
            ("t7", "task", 0, 8, 0, "Recibe la confirmación de la venta"),
            ("e1", "end", 0, 9, 0, "Licencia activa"),
        ],
        "flujos": [("s1", "t1"), ("t1", "t2"), ("t2", "g1"), ("g1", "t4", "Sí"), ("g1", "t3", "No"),
                   ("t3", "t4"), ("t4", "t5"), ("t5", "t6"), ("t6", "t7"), ("t7", "e1")],
    },
    {
        "id": "P2", "archivo": "P2_Alerta_de_vencimiento.bpmn", "nombre": "P2 Alerta de vencimiento",
        "carriles": ["Sistema AUTODEV", "Vendedor AUTODEV", "Cliente"],
        "nodos": [
            ("s1", "timer", 0, 0, 0, "Cada 24 horas y al iniciar sesión"),
            ("t1", "task", 0, 1, 0, "Actualiza el estado de las licencias"),
            ("g1", "xor", 0, 2, 0, "¿Hay licencias por vencer sin alerta?"),
            ("e0", "end", 0, 3, 1, "Sin alertas nuevas"),
            ("t2", "task", 0, 3, 0, "Genera la alerta y actualiza el contador"),
            ("t3", "task", 1, 4, 0, "Revisa la lista de alertas"),
            ("t4", "task", 1, 5, 0, "Contacta al cliente por WhatsApp"),
            ("t5", "task", 2, 6, 0, "Recibe el aviso de renovación"),
            ("g2", "xor", 2, 7, 0, "¿Desea renovar?"),
            ("t6", "task", 1, 8, 0, "Registra la renovación (P3)"),
            ("t7", "task", 1, 8, 1, "Marca la alerta como atendida"),
            ("e1", "end", 1, 9, 0, "Alerta atendida"),
        ],
        "flujos": [("s1", "t1"), ("t1", "g1"), ("g1", "t2", "Sí"), ("g1", "e0", "No"), ("t2", "t3"), ("t3", "t4"),
                   ("t4", "t5"), ("t5", "g2"), ("g2", "t6", "Sí", "vh"), ("g2", "t7", "No", "hv"),
                   ("t6", "e1"), ("t7", "e1")],
    },
    {
        "id": "P3", "archivo": "P3_Renovacion_de_licencia.bpmn", "nombre": "P3 Renovación de licencia",
        "carriles": ["Cliente", "Vendedor AUTODEV", "Sistema AUTODEV"],
        "nodos": [
            ("s1", "start", 0, 0, 0, "Acepta renovar su licencia"),
            ("t1", "task", 0, 1, 0, "Paga la renovación"),
            ("t2", "task", 1, 2, 0, "Elige la licencia y pulsa Renovar"),
            ("t3", "task", 2, 3, 0, "Busca la licencia anterior del mismo programa"),
            ("g1", "xor", 2, 4, 0, "¿La anterior aún no vence?"),
            ("t4", "task", 2, 5, 0, "Nueva emisión = vencimiento anterior"),
            ("t5", "task", 2, 5, 1, "Nueva emisión = fecha de pago"),
            ("t6", "task", 2, 6, 0, "Calcula vencimiento (+1 año) y guarda"),
            ("t7", "task", 2, 7, 0, "Marca la anterior como Renovada y cierra sus alertas"),
            ("e1", "end", 2, 8, 0, "Licencia renovada"),
        ],
        "flujos": [("s1", "t1"), ("t1", "t2"), ("t2", "t3"), ("t3", "g1"), ("g1", "t4", "Sí"), ("g1", "t5", "No"),
                   ("t4", "t6"), ("t5", "t6"), ("t6", "t7"), ("t7", "e1")],
    },
    {
        "id": "P4", "archivo": "P4_Gestion_de_clientes.bpmn", "nombre": "P4 Registro y actualización de clientes",
        "carriles": ["Vendedor AUTODEV", "Sistema AUTODEV"],
        "nodos": [
            ("s1", "start", 0, 0, 0, "Cliente nuevo o cambio de datos"),
            ("t1", "task", 0, 1, 0, "Ingresa nombre, RUC y contacto"),
            ("t2", "task", 1, 2, 0, "Valida nombre y RUC de 11 dígitos"),
            ("g1", "xor", 1, 3, 0, "¿Datos válidos y RUC no repetido?"),
            ("t3", "task", 1, 4, 1, "Muestra el error"),
            ("t4", "task", 1, 5, 0, "Guarda el cliente"),
            ("t5", "task", 0, 6, 0, "Ve al cliente en la lista para venderle"),
            ("e1", "end", 0, 7, 0, "Cliente registrado"),
        ],
        "flujos": [("s1", "t1"), ("t1", "t2"), ("t2", "g1"), ("g1", "t4", "Sí"), ("g1", "t3", "No"),
                   ("t3", "t1", "Corrige", "down"), ("t4", "t5"), ("t5", "e1")],
    },
    {
        "id": "P5", "archivo": "P5_Reporte_mensual.bpmn", "nombre": "P5 Reporte mensual de ventas y cartera",
        "carriles": ["Administrador", "Sistema AUTODEV"],
        "nodos": [
            ("s1", "start", 0, 0, 0, "Revisión de fin de mes"),
            ("t1", "task", 0, 1, 0, "Abre el Resumen y elige el mes"),
            ("t2", "task", 1, 2, 0, "Actualiza el estado de las licencias"),
            ("t3", "task", 1, 3, 0, "Calcula ingresos, meta y ventas por programa"),
            ("t4", "task", 1, 4, 0, "Cuenta vigentes, por vencer, vencidas y clientes activos"),
            ("t5", "task", 0, 5, 0, "Analiza el avance de la meta"),
            ("g1", "xor", 0, 6, 0, "¿Necesita el detalle?"),
            ("t6", "task", 0, 7, 0, "Exporta las licencias a Excel"),
            ("t7", "task", 1, 8, 0, "Genera el archivo .xlsx (Apache POI)"),
            ("e1", "end", 0, 9, 0, "Reporte revisado"),
        ],
        "flujos": [("s1", "t1"), ("t1", "t2"), ("t2", "t3"), ("t3", "t4"), ("t4", "t5"), ("t5", "g1"),
                   ("g1", "t6", "Sí"), ("g1", "e1", "No", "up"), ("t6", "t7"), ("t7", "e1")],
    },
]


def generar(p):
    x0, y0 = 20, 20
    cabecera = 30
    filas_por_carril = {}
    for _, _, carril, _, fila, _ in p["nodos"]:
        filas_por_carril[carril] = max(filas_por_carril.get(carril, 0), fila)
    alturas = [230 if filas_por_carril.get(i, 0) > 0 else 150 for i in range(len(p["carriles"]))]
    topes = [y0 + sum(alturas[:i]) for i in range(len(alturas))]
    columnas = max(n[3] for n in p["nodos"]) + 1
    ancho = cabecera + 30 + columnas * ANCHO_COL + 20

    pos = {}
    for nid, tipo, carril, col, fila, texto in p["nodos"]:
        cx = x0 + cabecera + 30 + 75 + col * ANCHO_COL
        cy = topes[carril] + (75 if fila == 0 else 175)
        w, h = TAM[tipo]
        pos[nid] = dict(tipo=tipo, carril=carril, cx=cx, cy=cy, w=w, h=h, texto=texto)

    def ruta(a, b, estilo):
        s, t = pos[a], pos[b]
        if estilo == "down":
            fondo = topes[s["carril"]] + alturas[s["carril"]] - 14
            return [(s["cx"], s["cy"] + s["h"] / 2), (s["cx"], fondo), (t["cx"], fondo), (t["cx"], t["cy"] + t["h"] / 2)] \
                if t["carril"] == s["carril"] else \
                [(s["cx"], s["cy"] + s["h"] / 2), (s["cx"], fondo), (t["cx"], fondo), (t["cx"], t["cy"] + t["h"] / 2)]
        if estilo == "up":
            techo = topes[s["carril"]] + 14
            return [(s["cx"], s["cy"] - s["h"] / 2), (s["cx"], techo), (t["cx"], techo), (t["cx"], t["cy"] - t["h"] / 2)]
        if s["cy"] == t["cy"]:
            return [(s["cx"] + s["w"] / 2, s["cy"]), (t["cx"] - t["w"] / 2, t["cy"])]
        if estilo is None:
            estilo = "vh" if s["tipo"] == "xor" else "hv"
        if estilo == "vh":
            sy = s["cy"] + (s["h"] / 2 if t["cy"] > s["cy"] else -s["h"] / 2)
            return [(s["cx"], sy), (s["cx"], t["cy"]), (t["cx"] - t["w"] / 2, t["cy"])]
        ty = t["cy"] - t["h"] / 2 if t["cy"] > s["cy"] else t["cy"] + t["h"] / 2
        return [(s["cx"] + s["w"] / 2, s["cy"]), (t["cx"], s["cy"]), (t["cx"], ty)]

    proc = [f'  <process id="Proceso_{p["id"]}" name="{escape(p["nombre"])}" isExecutable="false">',
            '    <laneSet id="Carriles_' + p["id"] + '">']
    for i, carril in enumerate(p["carriles"]):
        proc.append(f'      <lane id="Carril_{p["id"]}_{i}" name="{escape(carril)}">')
        for nid, n in pos.items():
            if n["carril"] == i:
                proc.append(f'        <flowNodeRef>{p["id"]}_{nid}</flowNodeRef>')
        proc.append('      </lane>')
    proc.append('    </laneSet>')

    entradas = {nid: [] for nid in pos}
    salidas = {nid: [] for nid in pos}
    flujos = []
    for i, f in enumerate(p["flujos"]):
        a, b = f[0], f[1]
        etiqueta = f[2] if len(f) > 2 else ""
        estilo = f[3] if len(f) > 3 else None
        fid = f'{p["id"]}_f{i + 1}'
        salidas[a].append(fid)
        entradas[b].append(fid)
        flujos.append((fid, a, b, etiqueta, estilo))

    etiquetas_xml = {"task": "task", "start": "startEvent", "timer": "startEvent", "end": "endEvent", "xor": "exclusiveGateway"}
    for nid, n in pos.items():
        tag = etiquetas_xml[n["tipo"]]
        proc.append(f'    <{tag} id="{p["id"]}_{nid}" name="{escape(n["texto"])}">')
        for e in entradas[nid]:
            proc.append(f'      <incoming>{e}</incoming>')
        for s in salidas[nid]:
            proc.append(f'      <outgoing>{s}</outgoing>')
        if n["tipo"] == "timer":
            proc.append(f'      <timerEventDefinition id="{p["id"]}_{nid}_timer"><timeCycle>R/P1D</timeCycle></timerEventDefinition>')
        proc.append(f'    </{tag}>')
    for fid, a, b, etiqueta, _ in flujos:
        nombre = f' name="{escape(etiqueta)}"' if etiqueta else ""
        proc.append(f'    <sequenceFlow id="{fid}"{nombre} sourceRef="{p["id"]}_{a}" targetRef="{p["id"]}_{b}" />')
    proc.append('  </process>')

    alto_total = sum(alturas)
    di = [f'  <bpmndi:BPMNDiagram id="Diagrama_{p["id"]}">',
          f'    <bpmndi:BPMNPlane id="Plano_{p["id"]}" bpmnElement="Colaboracion_{p["id"]}">',
          f'      <bpmndi:BPMNShape id="Pool_{p["id"]}_di" bpmnElement="Pool_{p["id"]}" isHorizontal="true">',
          f'        <dc:Bounds x="{x0}" y="{y0}" width="{ancho}" height="{alto_total}" />',
          '      </bpmndi:BPMNShape>']
    for i in range(len(p["carriles"])):
        di += [f'      <bpmndi:BPMNShape id="Carril_{p["id"]}_{i}_di" bpmnElement="Carril_{p["id"]}_{i}" isHorizontal="true">',
               f'        <dc:Bounds x="{x0 + cabecera}" y="{topes[i]}" width="{ancho - cabecera}" height="{alturas[i]}" />',
               '      </bpmndi:BPMNShape>']
    for nid, n in pos.items():
        x, y = n["cx"] - n["w"] / 2, n["cy"] - n["h"] / 2
        di.append(f'      <bpmndi:BPMNShape id="{p["id"]}_{nid}_di" bpmnElement="{p["id"]}_{nid}"'
                  + (' isMarkerVisible="true"' if n["tipo"] == "xor" else '') + '>')
        di.append(f'        <dc:Bounds x="{x:.0f}" y="{y:.0f}" width="{n["w"]}" height="{n["h"]}" />')
        if n["tipo"] != "task":
            # En las compuertas con una salida hacia abajo, el texto va arriba para no taparla
            baja = any(pos[b]["cy"] > n["cy"] or (est == "down") for _, a, b, _, est in flujos if a == nid)
            ly = y - 44 if n["tipo"] == "xor" and baja else y + n["h"] + 4
            di.append(f'        <bpmndi:BPMNLabel><dc:Bounds x="{n["cx"] - 55:.0f}" y="{ly:.0f}" width="110" height="40" /></bpmndi:BPMNLabel>')
        di.append('      </bpmndi:BPMNShape>')
    for fid, a, b, etiqueta, estilo in flujos:
        puntos = ruta(a, b, estilo)
        di.append(f'      <bpmndi:BPMNEdge id="{fid}_di" bpmnElement="{fid}">')
        for px, py in puntos:
            di.append(f'        <di:waypoint x="{px:.0f}" y="{py:.0f}" />')
        if etiqueta:
            (ax, ay), (bx, by) = puntos[0], puntos[1]
            lx = (ax + bx) / 2 + (6 if ax == bx else -12)
            ly = (ay + by) / 2 - (10 if ax == bx else 22)
            di.append(f'        <bpmndi:BPMNLabel><dc:Bounds x="{lx:.0f}" y="{ly:.0f}" width="50" height="16" /></bpmndi:BPMNLabel>')
        di.append('      </bpmndi:BPMNEdge>')
    di += ['    </bpmndi:BPMNPlane>', '  </bpmndi:BPMNDiagram>']

    xml = ['<?xml version="1.0" encoding="UTF-8"?>',
           '<definitions xmlns="http://www.omg.org/spec/BPMN/20100524/MODEL"'
           ' xmlns:bpmndi="http://www.omg.org/spec/BPMN/20100524/DI"'
           ' xmlns:dc="http://www.omg.org/spec/DD/20100524/DC"'
           ' xmlns:di="http://www.omg.org/spec/DD/20100524/DI"'
           f' id="Definiciones_{p["id"]}" targetNamespace="http://autodev.pe/bpmn">',
           f'  <collaboration id="Colaboracion_{p["id"]}">',
           f'    <participant id="Pool_{p["id"]}" name="{escape(p["nombre"])}" processRef="Proceso_{p["id"]}" />',
           '  </collaboration>']
    xml += proc + di + ['</definitions>', '']
    return "\n".join(xml), ancho + 2 * x0, alto_total + 2 * y0


if __name__ == "__main__":
    carpeta = Path(__file__).parent
    medidas = []
    for proceso in PROCESOS:
        contenido, w, h = generar(proceso)
        (carpeta / proceso["archivo"]).write_text(contenido, encoding="utf-8")
        medidas.append(f'{proceso["archivo"]} {w:.0f} {h:.0f}')
        print("Generado:", proceso["archivo"], int(w), "x", int(h))
    (carpeta / "medidas.txt").write_text("\n".join(medidas) + "\n", encoding="utf-8")
