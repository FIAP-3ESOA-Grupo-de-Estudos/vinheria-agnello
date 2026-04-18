<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" %>
    <% request.setAttribute("includedHeader", Boolean.TRUE); %> 
    <%@ include
    file="templates/common/header.jsp" %>
    <main>
      <div class="container">
        <section class="userprofile-section">
            <div class="userprofile-bg">
                <img
                src="assets/images/user-profile-bg.png"
                alt="User Profile Image"
                class="userprofile-image"
                />
            </div>
            <div class="userprofile-divider"><h1>MEU PERFIL</h1>
                <ul>
                    <li><strong>Nome:</strong> João Silva</li>
                    <li><strong>Email:</strong> joao.silva@example.com</li>
                    <li><strong>Membro desde:</strong> 15/03/2023</li>
                    <li><strong>Plano de assinatura:</strong> Explorador</li>
                    <li><strong>Vinhos aprovados:</strong> 6</li>
                    <li><strong>Favoritos:</strong> 2</li>
                    <li><strong>Nota Média:</strong> 4.6</li>
                </ul>
            </div>
        </section>
      </div>
      <%@ include file="templates/common/footer.jsp" %>
    </main>
  </body>
</html>
