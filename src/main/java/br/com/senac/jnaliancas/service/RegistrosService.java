package br.com.senac.jnaliancas.service;

import br.com.senac.jnaliancas.domain.*;
import br.com.senac.jnaliancas.repository.*;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** Adapta o formato das telas aos modelos relacionais persistidos com JPA. */
@Service
public class RegistrosService {
    private final ClienteRepository clientes;
    private final ProdutoRepository produtos;
    private final PedidoRepository pedidos;
    private final PagamentoRepository pagamentos;
    private final EntregaRepository entregas;
    private final AgendamentoRepository agendamentos;

    public RegistrosService(ClienteRepository clientes, ProdutoRepository produtos, PedidoRepository pedidos,
            PagamentoRepository pagamentos, EntregaRepository entregas, AgendamentoRepository agendamentos) {
        this.clientes=clientes; this.produtos=produtos; this.pedidos=pedidos;
        this.pagamentos=pagamentos; this.entregas=entregas; this.agendamentos=agendamentos;
    }

    @Transactional(readOnly=true)
    public List<Map<String,Object>> listar(String tipo) {
        return switch(tipo) {
            case "clientes" -> clientes.findAll().stream().map(this::mapa).toList();
            case "produtos" -> produtos.findAll().stream().map(this::mapa).toList();
            case "pedidos" -> pedidos.findAll().stream().map(this::mapa).toList();
            case "pagamentos" -> pagamentos.findAll().stream().map(this::mapa).toList();
            case "entregas" -> entregas.findAll().stream().map(this::mapa).toList();
            case "agendamentos" -> agendamentos.findAll().stream().map(this::mapa).toList();
            default -> throw naoEncontrado();
        };
    }

    @Transactional
    public List<Map<String,Object>> substituir(String tipo, List<Map<String,Object>> dados) {
        if (dados == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Envie uma lista JSON.");
        return switch(tipo) {
            case "clientes" -> sincronizarClientes(dados);
            case "produtos" -> sincronizarProdutos(dados);
            case "pedidos" -> sincronizarPedidos(dados);
            case "pagamentos" -> sincronizarPagamentos(dados);
            case "entregas" -> sincronizarEntregas(dados);
            case "agendamentos" -> sincronizarAgendamentos(dados);
            default -> throw naoEncontrado();
        };
    }

    @Transactional
    public Map<String,Object> salvarItem(String tipo, Long id, Map<String,Object> d) {
        if (d == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Envie um objeto JSON.");
        return switch(tipo) {
            case "clientes" -> { Cliente x=id==null?new Cliente():clientes.findById(id).orElseThrow(this::registroNaoEncontrado); preencher(x,d); yield mapa(clientes.save(x)); }
            case "produtos" -> { Produto x=id==null?new Produto():produtos.findById(id).orElseThrow(this::registroNaoEncontrado); preencher(x,d); yield mapa(produtos.save(x)); }
            case "pedidos" -> mapa(salvarPedido(id,d));
            case "pagamentos" -> { Pagamento x=id==null?new Pagamento():pagamentos.findById(id).orElseThrow(this::registroNaoEncontrado); preencher(x,d); yield mapa(pagamentos.save(x)); }
            case "entregas" -> { Entrega x=id==null?new Entrega():entregas.findById(id).orElseThrow(this::registroNaoEncontrado); preencher(x,d); yield mapa(entregas.save(x)); }
            case "agendamentos" -> { Agendamento x=id==null?new Agendamento():agendamentos.findById(id).orElseThrow(this::registroNaoEncontrado); preencher(x,d); yield mapa(agendamentos.save(x)); }
            default -> throw naoEncontrado();
        };
    }

    @Transactional
    public void removerItem(String tipo, Long id) {
        switch(tipo) {
            case "clientes" -> { Cliente x=clientes.findById(id).orElseThrow(this::registroNaoEncontrado); if(pedidos.existsByCliente_Id(id)||pagamentos.existsByCliente_Id(id)||entregas.existsByCliente_Id(id)||agendamentos.existsByCliente_Id(id)) throw new ResponseStatusException(HttpStatus.CONFLICT,"Cliente possui registros vinculados."); clientes.delete(x); }
            case "produtos" -> { Produto x=produtos.findById(id).orElseThrow(this::registroNaoEncontrado); if(pedidos.existsByProduto_Id(id)) throw new ResponseStatusException(HttpStatus.CONFLICT,"Produto possui pedidos vinculados."); produtos.delete(x); }
            case "pedidos" -> { Pedido x=pedidos.findById(id).orElseThrow(this::registroNaoEncontrado); x.produto.quantidade += x.quantidade; produtos.save(x.produto); pedidos.delete(x); }
            case "pagamentos" -> pagamentos.delete(pagamentos.findById(id).orElseThrow(this::registroNaoEncontrado));
            case "entregas" -> entregas.delete(entregas.findById(id).orElseThrow(this::registroNaoEncontrado));
            case "agendamentos" -> agendamentos.delete(agendamentos.findById(id).orElseThrow(this::registroNaoEncontrado));
            default -> throw naoEncontrado();
        }
    }

    private void preencher(Cliente x,Map<String,Object>d){x.nome=obrigatorio(d,"nome");x.nascimento=validarData(obrigatorio(d,"nascimento"));x.cpf=obrigatorio(d,"cpf");x.telefone=obrigatorio(d,"telefone");x.rua=obrigatorio(d,"rua");x.numero=obrigatorio(d,"numero");x.bairro=obrigatorio(d,"bairro");x.cidade=obrigatorio(d,"cidade");x.estado=obrigatorio(d,"estado");x.cep=obrigatorio(d,"cep");x.complemento=texto(d,"complemento");}
    private void preencher(Produto x,Map<String,Object>d){x.nome=obrigatorio(d,"nome");x.material=obrigatorio(d,"material");x.quantidade=inteiro(d,"quantidade");if(x.quantidade<0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Estoque não pode ser negativo.");x.preco=decimalValidado(d,"preco");x.categoria=obrigatorio(d,"categoria");x.observacoes=texto(d,"observacoes");}
    private Pedido salvarPedido(Long id,Map<String,Object>d){
        Pedido x=id==null?new Pedido():pedidos.findById(id).orElseThrow(this::registroNaoEncontrado);
        Produto estoqueAnterior=x.id==null?null:x.produto; int quantidadeAnterior=x.id==null?0:x.quantidade;
        Produto novoProduto=produtoPorNome(obrigatorio(d,"produto")); int novaQuantidade=inteiro(d,"quantidade");
        if(novaQuantidade<=0)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Quantidade deve ser maior que zero.");
        int disponivel=novoProduto.quantidade+(estoqueAnterior!=null&&estoqueAnterior.id.equals(novoProduto.id)?quantidadeAnterior:0);
        if(disponivel<novaQuantidade)throw new ResponseStatusException(HttpStatus.CONFLICT,"Estoque insuficiente.");
        if(estoqueAnterior!=null)estoqueAnterior.quantidade+=quantidadeAnterior;
        novoProduto.quantidade-=novaQuantidade;
        if(estoqueAnterior!=null)produtos.save(estoqueAnterior); produtos.save(novoProduto);
        x.cliente=clientePorNome(obrigatorio(d,"cliente")); x.produto=novoProduto; x.quantidade=novaQuantidade;
        x.data=validarData(obrigatorio(d,"data")); x.observacoes=texto(d,"observacoes");
        x.concluido=texto(d,"concluido").isBlank()?"Não":texto(d,"concluido");
        x.total=novoProduto.preco.multiply(BigDecimal.valueOf(novaQuantidade)).setScale(2,java.math.RoundingMode.HALF_UP);
        return pedidos.save(x);
    }
    private void preencher(Pagamento x,Map<String,Object>d){x.cliente=clientePorNome(obrigatorio(d,"cliente"));x.valor=decimalValidado(d,"valor");x.tipo=obrigatorio(d,"tipo");x.pago=decimalValidado(d,"pago");x.pendente=x.valor.subtract(x.pago).max(BigDecimal.ZERO);}
    private void preencher(Entrega x,Map<String,Object>d){x.cliente=clientePorNome(obrigatorio(d,"cliente"));x.tipo=obrigatorio(d,"tipo");x.frete=texto(d,"frete").isBlank()?null:decimalValidado(d,"frete");x.prazo=obrigatorio(d,"prazo");x.observacoes=texto(d,"observacoes");}
    private void preencher(Agendamento x,Map<String,Object>d){x.cliente=clientePorNome(obrigatorio(d,"cliente"));x.data=validarData(obrigatorio(d,"data"));x.hora=obrigatorio(d,"hora");if(!br.com.senac.jnaliancas.util.RegrasNegocio.validarHora(x.hora))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Horário inválido.");x.observacoes=texto(d,"observacoes");}
    private String validarData(String v){if(!br.com.senac.jnaliancas.util.RegrasNegocio.validarData(v))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Data inválida; use dd/MM/aaaa.");return v;}
    private BigDecimal decimalValidado(Map<String,Object>d,String k){String v=obrigatorio(d,k);if(!br.com.senac.jnaliancas.util.RegrasNegocio.validarValorMonetario(v))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Valor inválido; use o formato 00.00.");return new BigDecimal(v);}
    private ResponseStatusException registroNaoEncontrado(){return new ResponseStatusException(HttpStatus.NOT_FOUND,"Registro não encontrado.");}

    private List<Map<String,Object>> sincronizarClientes(List<Map<String,Object>> dados) {
        List<Cliente> antes=clientes.findAll(); Set<Long> manter=new HashSet<>();
        for(var d:dados){ Cliente x=clientesPorId(d); x.nome=obrigatorio(d,"nome"); x.nascimento=obrigatorio(d,"nascimento");
            x.cpf=obrigatorio(d,"cpf"); x.telefone=obrigatorio(d,"telefone"); x.rua=obrigatorio(d,"rua");
            x.numero=obrigatorio(d,"numero"); x.bairro=obrigatorio(d,"bairro"); x.cidade=obrigatorio(d,"cidade");
            x.estado=obrigatorio(d,"estado"); x.cep=obrigatorio(d,"cep"); x.complemento=texto(d,"complemento");
            manter.add(clientes.save(x).id); }
        for(Cliente x:antes) if(!manter.contains(x.id)) {
            Long id=x.id;
            if(pedidos.existsByCliente_Id(id)||pagamentos.existsByCliente_Id(id)||entregas.existsByCliente_Id(id)||agendamentos.existsByCliente_Id(id))
                throw new ResponseStatusException(HttpStatus.CONFLICT,"Cliente possui registros vinculados e não pode ser removido.");
            clientes.delete(x);
        }
        return listar("clientes");
    }

    private List<Map<String,Object>> sincronizarProdutos(List<Map<String,Object>> dados) {
        List<Produto> antes=produtos.findAll(); Set<Long> manter=new HashSet<>();
        for(var d:dados){ Produto x=produtoPorId(d); x.nome=obrigatorio(d,"nome"); x.material=obrigatorio(d,"material");
            x.quantidade=inteiro(d,"quantidade"); x.preco=decimal(d,"preco"); x.categoria=obrigatorio(d,"categoria");
            x.observacoes=texto(d,"observacoes"); manter.add(produtos.save(x).id); }
        for(Produto x:antes) if(!manter.contains(x.id)) {
            if(pedidos.existsByProduto_Id(x.id)) throw new ResponseStatusException(HttpStatus.CONFLICT,"Produto possui pedidos e não pode ser removido.");
            produtos.delete(x);
        }
        return listar("produtos");
    }

    private List<Map<String,Object>> sincronizarPedidos(List<Map<String,Object>> dados) {
        List<Pedido> antes=pedidos.findAll(); Set<Long> manter=new HashSet<>();
        for(var d:dados){ Pedido x=pedidoPorId(d); x.cliente=clientePorNome(obrigatorio(d,"cliente"));
            x.produto=produtoPorNome(obrigatorio(d,"produto")); x.quantidade=inteiro(d,"quantidade");
            if(x.quantidade<=0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Quantidade deve ser maior que zero.");
            x.data=obrigatorio(d,"data"); x.observacoes=texto(d,"observacoes"); x.concluido=texto(d,"concluido").isBlank()?"Não":texto(d,"concluido");
            x.total=decimal(d,"total",x.produto.preco.multiply(BigDecimal.valueOf(x.quantidade)));
            manter.add(pedidos.save(x).id); }
        for(Pedido x:antes) if(!manter.contains(x.id)) pedidos.delete(x);
        return listar("pedidos");
    }

    private List<Map<String,Object>> sincronizarPagamentos(List<Map<String,Object>> dados) {
        List<Pagamento> antes=pagamentos.findAll(); Set<Long> manter=new HashSet<>();
        for(var d:dados){ Pagamento x=pagamentoPorId(d); x.cliente=clientePorNome(obrigatorio(d,"cliente"));
            x.valor=decimal(d,"valor"); x.tipo=obrigatorio(d,"tipo"); x.pago=decimal(d,"pago");
            x.pendente=decimal(d,"pendente",x.valor.subtract(x.pago).max(BigDecimal.ZERO)); manter.add(pagamentos.save(x).id); }
        for(Pagamento x:antes) if(!manter.contains(x.id)) pagamentos.delete(x);
        return listar("pagamentos");
    }

    private List<Map<String,Object>> sincronizarEntregas(List<Map<String,Object>> dados) {
        List<Entrega> antes=entregas.findAll(); Set<Long> manter=new HashSet<>();
        for(var d:dados){ Entrega x=entregaPorId(d); x.cliente=clientePorNome(obrigatorio(d,"cliente"));
            x.tipo=obrigatorio(d,"tipo"); x.frete=texto(d,"frete").isBlank()?null:decimal(d,"frete");
            x.prazo=obrigatorio(d,"prazo"); x.observacoes=texto(d,"observacoes"); manter.add(entregas.save(x).id); }
        for(Entrega x:antes) if(!manter.contains(x.id)) entregas.delete(x);
        return listar("entregas");
    }

    private List<Map<String,Object>> sincronizarAgendamentos(List<Map<String,Object>> dados) {
        List<Agendamento> antes=agendamentos.findAll(); Set<Long> manter=new HashSet<>();
        for(var d:dados){ Agendamento x=agendamentoPorId(d); x.cliente=clientePorNome(obrigatorio(d,"cliente"));
            x.data=obrigatorio(d,"data"); x.hora=obrigatorio(d,"hora"); x.observacoes=texto(d,"observacoes"); manter.add(agendamentos.save(x).id); }
        for(Agendamento x:antes) if(!manter.contains(x.id)) agendamentos.delete(x);
        return listar("agendamentos");
    }

    private Cliente clientesPorId(Map<String,Object>d){return existente(d,clientes.findAll(),Cliente.class);}
    private Produto produtoPorId(Map<String,Object>d){return existente(d,produtos.findAll(),Produto.class);}
    private Pedido pedidoPorId(Map<String,Object>d){return existente(d,pedidos.findAll(),Pedido.class);}
    private Pagamento pagamentoPorId(Map<String,Object>d){return existente(d,pagamentos.findAll(),Pagamento.class);}
    private Entrega entregaPorId(Map<String,Object>d){return existente(d,entregas.findAll(),Entrega.class);}
    private Agendamento agendamentoPorId(Map<String,Object>d){return existente(d,agendamentos.findAll(),Agendamento.class);}

    private <T extends RegistroBase> T existente(Map<String,Object>d,List<T> lista,Class<T> tipo){
        Long id=id(d); if(id!=null) for(T x:lista) if(id.equals(x.id)) return x;
        try{return tipo.getDeclaredConstructor().newInstance();}catch(Exception e){throw new IllegalStateException(e);}
    }
    private Cliente clientePorNome(String nome){Cliente x=clientes.findFirstByNomeIgnoreCase(nome); if(x==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Cliente não cadastrado: "+nome); return x;}
    private Produto produtoPorNome(String nome){Produto x=produtos.findFirstByNomeIgnoreCase(nome); if(x==null)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Produto não cadastrado: "+nome); return x;}

    private Map<String,Object> mapa(Cliente x){return map(x.id,"nome",x.nome,"nascimento",x.nascimento,"cpf",x.cpf,"telefone",x.telefone,"rua",x.rua,"numero",x.numero,"bairro",x.bairro,"cidade",x.cidade,"estado",x.estado,"cep",x.cep,"complemento",x.complemento);}
    private Map<String,Object> mapa(Produto x){return map(x.id,"nome",x.nome,"material",x.material,"quantidade",str(x.quantidade),"preco",money(x.preco),"categoria",x.categoria,"observacoes",x.observacoes);}
    private Map<String,Object> mapa(Pedido x){return map(x.id,"cliente",x.cliente.nome,"produto",x.produto.nome,"quantidade",str(x.quantidade),"data",x.data,"observacoes",x.observacoes,"concluido",x.concluido,"total",money(x.total));}
    private Map<String,Object> mapa(Pagamento x){return map(x.id,"cliente",x.cliente.nome,"valor",money(x.valor),"tipo",x.tipo,"pago",money(x.pago),"pendente",money(x.pendente));}
    private Map<String,Object> mapa(Entrega x){return map(x.id,"cliente",x.cliente.nome,"tipo",x.tipo,"frete",x.frete==null?"":money(x.frete),"prazo",x.prazo,"observacoes",x.observacoes);}
    private Map<String,Object> mapa(Agendamento x){return map(x.id,"cliente",x.cliente.nome,"data",x.data,"hora",x.hora,"observacoes",x.observacoes);}
    private Map<String,Object> map(Long id,Object... pares){Map<String,Object> m=new LinkedHashMap<>();m.put("id",id);for(int i=0;i<pares.length;i+=2)m.put((String)pares[i],pares[i+1]);return m;}
    private String money(BigDecimal v){return v==null?"0.00":v.setScale(2,java.math.RoundingMode.HALF_UP).toPlainString();}
    private String str(Object x){return x==null?"":String.valueOf(x);}
    private String texto(Map<String,Object>d,String k){return str(d.get(k)).trim();}
    private String obrigatorio(Map<String,Object>d,String k){String v=texto(d,k);if(v.isBlank())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Campo obrigatório: "+k);return v;}
    private Long id(Map<String,Object>d){Object v=d.get("id");if(v==null||v.toString().isBlank())return null;try{return Long.valueOf(v.toString());}catch(Exception e){return null;}}
    private int inteiro(Map<String,Object>d,String k){try{return Integer.parseInt(obrigatorio(d,k));}catch(NumberFormatException e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Número inválido: "+k);}}
    private BigDecimal decimal(Map<String,Object>d,String k){return decimal(d,k,null);}
    private BigDecimal decimal(Map<String,Object>d,String k,BigDecimal padrao){String v=texto(d,k);if(v.isBlank()&&padrao!=null)return padrao;try{return new BigDecimal(obrigatorio(d,k));}catch(NumberFormatException e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Valor inválido: "+k);}}
    private ResponseStatusException naoEncontrado(){return new ResponseStatusException(HttpStatus.NOT_FOUND,"Módulo não encontrado.");}
}
