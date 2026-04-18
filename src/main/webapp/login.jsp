<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" %>
    <% request.setAttribute("includedHeader", Boolean.TRUE); %> 
    <%@ include
    file="templates/common/header.jsp" %>
    <main>
      <div class="container">
        <section class="login-section">
          <div class="login-bg">
            <img
              src="assets/images/login-page-bg.png"
              alt="Login Image"
              class="login-image"
            />
          </div>
          <div class="login-divider"><h1>ACESSE SUA CONTA</h1>
            <form action="${pageContext.request.contextPath}/auth/login" method="post" class="login-form">
                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required />
                <label for="password">Senha:</label>
                <input type="password" id="password" name="password" required />
                <div class="login-footer"><span>Esqueceu sua senha?</span>
                <button type="submit" class="login-btn">Entrar</button></div>
            </form>
          </div>
        </section>          
      </div>
      <%@ include file="templates/common/footer.jsp" %>
    </main>
  </body>
</html>
