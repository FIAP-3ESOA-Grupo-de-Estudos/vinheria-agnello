<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" %>
    <% request.setAttribute("includedHeader", Boolean.TRUE); %> 
    <%@ include
    file="templates/common/header.jsp" %>
    <main>
      <div class="container">
        <section class="userprofile-section">
            
            
                <div class="sidebar">
                    <div class="userprofile-bg">
                        <div class="profile-top">
                            <span class="profile-initials">JS</span>
                            <div>
                                <h1>João Silva</h1>
                                <h2>Membro desde 15/03/2023</h2>
                            </div>
                        </div>
                        <ul>
                            <li><a href="#">Meus Vinhos Favoritos</a></li>
                            <li><a href="#">Histórico de Pedidos</a></li>
                            <li><a href="#">Clube de Benefícios</a></li>
                            <li><a href="#">Configurações</a></li>
                            <li><a href="#">Sair</a></li>
                        </ul>
                    </div>
                    <div class="trophies">
                        <h1>Conquistas</h1>
                        <ul>
                            <li>
                                <img src="https://placehold.co/64x64" alt="Trophy 1" />
                                <span>Primeiro Vinho</span>
                            </li>
                            <li>
                                <img src="https://placehold.co/64x64" alt="Trophy 2" />
                                <span>10 Avaliações</span>
                            </li>
                            <li>
                                <img src="https://placehold.co/64x64" alt="Trophy 3" />
                                <span>Explorador</span>
                            </li>
                            <li>
                                <img src="https://placehold.co/64x64" alt="Trophy 4" />
                                <span>Harmonizador</span>
                            </li>
                            <li>
                                <img src="https://placehold.co/64x64" alt="Trophy 5" />
                                <span>Sommelier</span>
                            </li>
                        </ul>
                    </div>
                </div>
                <div class="main-content">
                    <div class="featured">
                        <ul>
                            <li>
                                <span>SUA ASSINATURA</span>
                                <h1>Plano Explorador</h1>
                                <p>4 garrafas/mês + 1 surpresa · R$ 179/mês</p>
                                <button>Gerenciar Assinatura</button>
                            </li>
                        </ul>
                    </div>
                    <div class="profile-analysis">
                        <section class="block">
                        <ul>
                            <li><span>23</span><span>Vinhos aprovados</span></li>
                            <li><span>12</span><span>Favoritos</span></li>
                            <li><span>4.8</span><span>Nota Média</span></li>
                        </ul>
                        <h1>Seu Perfil de Sabor</h1>
                        <ul class="progress-bar">
                            <li><label>Tinto:</label><progress value="80" max="100"></progress></li>
                            <li><label>Branco:</label><progress value="60" max="100"></progress></li>
                            <li><label>Rosé:</label><progress value="40" max="100"></progress></li>
                            <li><label>Espumante:</label><progress value="20" max="100"></progress></li>
                            <li><label>Suave:</label><progress value="30" max="100"></progress></li>
                        </ul>
                        </section>
                    </div>
                </div>
                
            </div>
        </section>
      </div>
      <%@ include file="templates/common/footer.jsp" %>
    </main>
  </body>
</html>
