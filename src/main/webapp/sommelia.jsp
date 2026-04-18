<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" %>
    <% request.setAttribute("includedHeader", Boolean.TRUE); %> 
    <%@ include
    file="templates/common/header.jsp" %>
    <main>
      <div class="container">
        <section class="ia-assistant-section">
            
            
                <div class="sidebar">
                    <span class="assistant-icon">🤖</span>
                    <h1>Somel<span>ia</span></h1>
                    <h2>Sua sommelier virtual</h2>
                    <p>A SomelIA conhece mais de 150 rótulos do catálogo Agnello e aprende com o seu paladar a cada compra.</p>
                    <h3>Sugestões Rápidas</h3>
                    <ul>
                        <li>🍽️  Harmonização Inversa</li>
                        <li>🎁  Vinho para Presente</li>
                        <li>🆕  Descobrir Novos</li>
                        <li>🏆  Top Avaliados</li>
                    </ul>
                </div>
                <div class="main-content">
                    <h1>🤖  SomelIA · Online</h1>
                    <section class="assistant-box">
                        <div class="messages">
                        <div class="assistant-message">
                            <p>Olá, Felipe! Eu sou a SomelIA, sua sommelier virtual especializada em vinhos Agnello. 🍷 </br></br>Estou aqui para ajudar você a descobrir os melhores rótulos, harmonizações perfeitas e recomendações personalizadas. O que você gostaria de explorar hoje?</p>
                        </div>
                        <div class="user-message">
                            <p>Quero um vinho que armonize com queijos.</p>
                        </div>
                        <div class="assistant-message">
                            <p>Excelente escolha, Felipe! Harmonizar vinhos com queijos é uma arte deliciosa. 🍷🧀 Aqui vai uma guia rápida baseada nos tipos de queijo:</p>
                            <ul>
                                <li><strong>Queijos suaves e cremosos</strong> (como Brie ou Camembert): Combine com vinhos brancos leves e frescos, como um Sauvignon Blanc ou Chardonnay jovem. Eles equilibram a cremosidade sem overpower.</li>
                                <li><strong>Queijos duros e envelhecidos</strong> (como Cheddar ou Parmigiano-Reggiano): Vinhos tintos médios, como um Cabernet Sauvignon ou Merlot, que têm taninos suficientes para cortar a gordura.</li>
                                <li><strong>Queijos azuis</strong> (como Roquefort ou Gorgonzola): Vinhos doces ou fortificados, como um Porto ou Sauternes, que contrastam perfeitamente com o salgado e pungente.</li>
                                <li><strong>Queijos de cabra</strong>: Vinhos brancos secos e minerais, como um Riesling ou Albariño, para realçar a acidez.</li>
                            </ul>
                            <p>No catálogo Agnello, recomendo nosso Sauvignon Blanc para queijos suaves ou o Cabernet Sauvignon para os mais intensos. Qual tipo de queijo você tem em mente? Posso sugerir rótulos específicos! 😊</p>
                        </div>
                    </div>
                    <div class="footer">
                        <input type="text" placeholder="Pergunte à Sommelia..."><button>↗</button>
                    </div>
                    </section>
                </div>
                
            </div>
        </section>
      </div>
      <%@ include file="templates/common/footer.jsp" %>
    </main>
  </body>
</html>
