'use strict';
/* JN Alianças – comportamento das páginas integrado à API Spring/JPA. */
const $ = (s, r = document) => r.querySelector(s);
const $$ = (s, r = document) => [...r.querySelectorAll(s)];
const B = document.body, page = B.dataset.page, ent = B.dataset.entity;
const DB = {
  request: (method, url, body) => { const x=new XMLHttpRequest(); x.open(method,url,false); x.setRequestHeader('Content-Type','application/json'); const auth=sessionStorage.getItem('jn_basic'); if(auth)x.setRequestHeader('Authorization',`Basic ${auth}`); x.send(body===undefined?null:JSON.stringify(body)); if(x.status<200||x.status>=300)throw Error(x.status===401?'Entre novamente para salvar.':(x.responseText||`Erro HTTP ${x.status}`)); return x.responseText?JSON.parse(x.responseText):null; },
  get: k => { try { return DB.request('GET',`/api/registros/${encodeURIComponent(k)}`)||[]; } catch(e) { console.error('Falha ao consultar API',e); return []; } },
  set: (k, values) => { const old=DB.get(k), nextIds=new Set(); for(const item of values){ let current=item.id?old.find(x=>String(x.id)===String(item.id)):null; if(!current){const created=DB.request('POST',`/api/registros/${encodeURIComponent(k)}`,item); item.id=created.id; nextIds.add(String(item.id));} else {nextIds.add(String(current.id)); const changed=Object.keys(current).some(key=>String(current[key]??'')!==String(item[key]??'')); if(changed)DB.request('PUT',`/api/registros/${encodeURIComponent(k)}/${current.id}`,item);} } for(const item of old)if(!nextIds.has(String(item.id)))DB.request('DELETE',`/api/registros/${encodeURIComponent(k)}/${item.id}`); }
};
const igual = (a, b) => String(a).trim().toLowerCase() === String(b).trim().toLowerCase();
const esc = s => String(s ?? '').replace(/[&<>"]/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' }[c]));

/* ---------- Regras de validação (mesmas telas Java) ---------- */
const dataOk = s => {
  const m = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(s);
  if (!m) return false;
  const d = new Date(+m[3], m[2] - 1, +m[1]);
  return d.getFullYear() === +m[3] && d.getMonth() === m[2] - 1 && d.getDate() === +m[1];
};
const LETRAS = /^[a-zA-ZÀ-ÿ ]+$/;
const V = {
  nome: [s => LETRAS.test(s), 'Nome inválido! Digite apenas letras e espaços.'],
  cidade: [s => LETRAS.test(s), 'Cidade inválida! Digite apenas letras e espaços.'],
  qtd: [s => /^\d+$/.test(s), 'Quantidade inválida! Digite apenas números.'],
  numero: [s => /^[0-9]+$/.test(s), 'Número inválido! Digite apenas números.'],
  preco: [s => /^\d+\.\d{1,2}$/.test(s), 'Preço inválido! Insira no formato: 00.00'],
  valor: [s => /^\d+\.\d{1,2}$/.test(s), 'Valor inválido! Insira no formato: 00.00'],
  frete: [s => /^\d+\.\d{1,2}$/.test(s), 'Frete inválido! Insira no formato: 00.00'],
  data: [dataOk, 'Data inválida! Insira no formato: dd/mm/aaaa'],
  hora: [s => /^([01]\d|2[0-3]):[0-5]\d$/.test(s), 'Horário inválido! Insira no formato: 00:00'],
  cpf: [s => /^\d{3}\.\d{3}\.\d{3}-\d{2}$/.test(s), 'CPF inválido! Insira no formato: 000.000.000-00'],
  tel: [s => /^\+\d{1,3}\s\(\d+\)[\d\s-]+$/.test(s), 'Número inválido! Insira no formato: +55 (00) 00000-0000 ou no formato do país correspondente.'],
  estado: [s => /^[A-Z]{2}$/.test(s), 'Estado inválido! Digite no formato: AA'],
  cep: [s => /^\d{5}-\d{3}$/.test(s), 'CEP inválido! Digite no formato: 00000-000'],
  prazo: [s => /^\d{1,2} dias úteis$/.test(s), 'Prazo inválido! Insira no formato: 00 dias úteis'],
  cliente: [s => DB.get('clientes').some(c => igual(c.nome, s)), 'Cliente inválido! Pessoa não cadastrada.'],
  produto: [s => DB.get('produtos').some(p => igual(p.nome, s)), 'Produto inválido! Item não cadastrado.']
};

/* ---------- Caixa de mensagem (como o JOptionPane do app Java) ---------- */
function caixa(texto, confirmar) {
  return new Promise(resolve => {
    const d = document.createElement('dialog');
    d.setAttribute('aria-labelledby', 'dlg-t');
    d.innerHTML = `<div class="dlg-barra" id="dlg-t">JN Alianças</div><p class="dlg-txt" role="alert">${esc(texto)}</p>
      <div class="dlg-btns">${confirmar ? '<button class="btn claro" value="nao">Não</button>' : ''}<button class="btn" value="sim" autofocus>${confirmar ? 'Sim' : 'OK'}</button></div>`;
    d.onclose = () => { const r = d.returnValue === 'sim'; d.remove(); resolve(r); };
    d.onclick = e => { if (e.target.value) { d.returnValue = e.target.value; d.close(); } };
    document.body.appendChild(d);
    d.showModal();
  });
}

/* ---------- Sessão ---------- */
if (page !== 'login' && !sessionStorage.getItem('jn_user')) location.replace('index.html');
$$('[data-sair]').forEach(a => a.addEventListener('click', () => { sessionStorage.removeItem('jn_user'); sessionStorage.removeItem('jn_basic'); }));

if (page === 'login') login();
else if (page === 'cadastro') cadastro();
else if (page === 'lista') lista();
else if (page === 'busca') busca();

function login() {
  const f = $('#f');
  f.onsubmit = e => {
    e.preventDefault();
    if (!f.login.value.trim() || !f.senha.value) return caixa('Informe login e senha.');
    const login=f.login.value.trim(), senha=f.senha.value;
    const basic=btoa(`${login}:${senha}`); const x = new XMLHttpRequest(); x.open('GET','/api/sessao',false); x.setRequestHeader('Authorization',`Basic ${basic}`); x.send();
    if (x.status === 200) { sessionStorage.setItem('jn_user', login); sessionStorage.setItem('jn_basic',basic); location.href = 'menu.html'; }
    else caixa('Login ou senha incorretos.');
  };
  $('#esqueci').onclick = () => caixa('Procure o administrador do sistema para redefinir a senha.');
}

/* ---------- Regras por entidade ---------- */
const ANTES = {
  pedidos(d, antigo) { // valida previamente; a baixa efetiva e transacional ocorre no serviço JPA
    const ps = DB.get('produtos');
    const p = ps.find(x => igual(x.nome, d.produto));
    if (!p) return 'Produto inválido! Item não cadastrado.';
    if (+d.quantidade <= 0) return 'Quantidade inválida! Digite um valor maior que zero.';
    const reservaAnterior = antigo && igual(antigo.produto, p.nome) ? +antigo.quantidade : 0;
    if (+p.quantidade + reservaAnterior < +d.quantidade) return 'Estoque insuficiente!';
    d.produto = p.nome;
    d.total = (parseFloat(p.preco) * +d.quantidade).toFixed(2);
  },
  pagamentos(d) { d.pendente = Math.max(0, Math.round((parseFloat(d.valor) - parseFloat(d.pago)) * 100) / 100).toFixed(2); }
};
const canon = d => { // grava o nome do cliente/produto como cadastrado
  if (d.cliente) d.cliente = DB.get('clientes').find(c => igual(c.nome, d.cliente)).nome;
};

/* ---------- Cadastro ---------- */
function cadastro() {
  const f = $('#f'), id = +new URLSearchParams(location.search).get('id') || 0;
  $$('datalist[data-src]').forEach(dl => { dl.innerHTML = DB.get(dl.dataset.src).map(x => `<option value="${esc(x.nome)}">`).join(''); });
  const antigo = id ? DB.get(ent).find(x => x.id === id) : null;
  if (antigo) {
    for (const el of f.elements) if (el.name && antigo[el.name] !== undefined) {
      if (el.type === 'checkbox') el.checked = antigo[el.name] === 'Sim'; else el.value = antigo[el.name];
    }
    $('#salvar').textContent = 'Salvar alterações';
  }
  f.onsubmit = async e => {
    e.preventDefault();
    const els = [...f.elements].filter(el => el.name);
    els.forEach(el => el.classList.remove('erro'));
    const falha = (el, m) => { el.classList.add('erro'); caixa(m).then(() => el.focus()); };
    const vazio = els.find(el => el.required && !el.value.trim());
    if (vazio) return falha(vazio, 'Todos os campos obrigatórios devem ser preenchidos.');
    const d = {};
    for (const el of els) {
      const v = el.type === 'checkbox' ? (el.checked ? 'Sim' : 'Não') : el.value.trim();
      const r = V[el.dataset.v];
      if (r && v && !r[0](v)) return falha(el, el.dataset.msg || r[1]);
      d[el.name] = v;
    }
    canon(d);
    const erro = ANTES[ent] && ANTES[ent](d, antigo);
    if (erro) return caixa(erro);
    const l = DB.get(ent);
    if (antigo) DB.set(ent, l.map(x => x.id === id ? { ...x, ...d } : x));
    else { l.push(d); DB.set(ent, l); }
    await caixa(antigo ? 'Dados atualizados com sucesso!' : f.dataset.ok);
    if (antigo) location.href = f.dataset.lista; else { f.reset(); els[0].focus(); }
  };
}

/* ---------- Lista ---------- */
function lista() {
  const tb = $('#t tbody'), cols = $$('#t th[data-k]').map(h => h.dataset.k), modo = B.dataset.mode;
  let sel = null;
  const dados = () => { const l = DB.get(ent); return modo === 'vendas' ? l.map(p => ({ ...p, cliente: p.cliente })) : l; };
  const render = () => {
    const l = dados();
    tb.innerHTML = l.length ? l.map(r => `<tr tabindex="0" data-id="${r.id}" aria-selected="false">${cols.map(k => `<td>${esc(r[k])}</td>`).join('')}</tr>`).join('')
      : `<tr><td colspan="${cols.length}" class="vazio">Nada cadastrado ainda.</td></tr>`;
    sel = null;
  };
  const marcar = tr => { if (!tr || !tr.dataset.id) return; $$('tr', tb).forEach(x => x.setAttribute('aria-selected', 'false')); tr.setAttribute('aria-selected', 'true'); sel = +tr.dataset.id; };
  tb.onclick = e => marcar(e.target.closest('tr'));
  tb.onkeydown = e => { if (e.key === 'Enter' || e.key === ' ') { e.preventDefault(); marcar(e.target.closest('tr')); } };
  const exigir = () => { if (sel) return DB.get(ent).find(x => x.id === sel); caixa('Selecione um item da lista.'); return null; };
  const alt = $('#alterar');
  if (alt) alt.onclick = () => {
    const r = exigir(); if (!r) return;
    const dest = ent === 'entregas' ? (r.tipo === 'Envio' ? 'entrega-envio.html' : 'entrega-retirada.html') : alt.dataset.destino;
    location.href = `${dest}?id=${r.id}`;
  };
  const rem = $('#remover');
  if (rem) rem.onclick = async () => {
    const r = exigir(); if (!r) return;
    if (!await caixa('Deseja remover o item selecionado?', true)) return;
    DB.set(ent, DB.get(ent).filter(x => x.id !== r.id));
    render(); caixa('Item removido com sucesso!');
  };
  render();
}

/* ---------- Busca ---------- */
function busca() {
  const op = $('#opcao'), nome = $('#nome'), rot = $('#rotulo');
  const porProduto = () => ['Produto', 'Estoque'].includes(op.value);
  op.onchange = () => { rot.textContent = porProduto() ? 'Digite o nome do produto:' : 'Digite o nome do cliente:'; };
  $('#f').onsubmit = e => {
    e.preventDefault();
    const q = nome.value.trim();
    if (!q) return caixa('O campo deve ser preenchido.');
    if (!LETRAS.test(q)) return caixa('Nome inválido! Digite apenas letras e espaços.');
    const t = op.value;
    if (porProduto()) {
      const p = DB.get('produtos').find(x => igual(x.nome, q));
      if (!p) return caixa('Produto inválido! Item não cadastrado.');
      return caixa(t === 'Produto' ? `Dados do Produto\nNome: ${p.nome}\nMaterial: ${p.material}\nQuantidade: ${p.quantidade}\nPreço: ${p.preco}\nCategoria: ${p.categoria}`
        : `Dados do Estoque\nNome: ${p.nome}\nQuantidade: ${p.quantidade}`);
    }
    const c = DB.get('clientes').find(x => igual(x.nome, q));
    if (!c) return caixa('Cliente inválido! Pessoa não cadastrada.');
    if (t === 'Cliente') {
      const [dd, mm, aa] = c.nascimento.split('/').map(Number), h = new Date();
      const idade = h.getFullYear() - aa - ((h.getMonth() + 1 < mm || (h.getMonth() + 1 === mm && h.getDate() < dd)) ? 1 : 0);
      return caixa(`Dados do Cliente\nNome: ${c.nome}\nIdade: ${idade}\nCPF: ${c.cpf}\nTelefone: ${c.telefone}\nEndereço: Rua ${c.rua}, ${c.numero} - ${c.bairro}, ${c.cidade}/${c.estado} - CEP ${c.cep}`);
    }
    const modelo = { Pedido: ['pedidos', 'Dados do Pedido'], Venda: ['pedidos', 'Dados da Venda'], Pagamento: ['pagamentos', 'Dados do Pagamento'],
      Entrega: ['entregas', 'Dados da Entrega'], Agendamento: ['agendamentos', 'Dados do Agendamento'] }[t];
    const r = DB.get(modelo[0]).find(x => igual(x.cliente, q));
    if (!r) return caixa(`Nenhum registro de ${t.toLowerCase()} encontrado para este cliente.`);
    const linhas = Object.entries(r).filter(([k, v]) => k !== 'id' && v !== '').map(([k, v]) => `${k.charAt(0).toUpperCase() + k.slice(1)}: ${v}`);
    caixa(`${modelo[1]}\n${linhas.join('\n')}`);
  };
}
