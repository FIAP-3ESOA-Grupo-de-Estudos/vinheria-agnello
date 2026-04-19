<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
  if (request.getAttribute("javax.servlet.include.request_uri") == null
      && !Boolean.TRUE.equals(request.getAttribute("includedHeader"))) {
    response.sendError(javax.servlet.http.HttpServletResponse.SC_FORBIDDEN);
    return;
  }
%>
<!doctype html>
<html lang="pt-BR">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Vinheria Agnelo</title>
    <link rel="stylesheet" href="assets/css/style.css?<%= System.currentTimeMillis() %>" />
  </head>
  <body>
    <header id="main-header">
      <div class="container">
        <a href="index.jsp"><img src="assets/images/logo.png" alt="Logo" class="logo" /></a> 
        <nav>
          <ul>
            <li><a href="index.jsp"><span class="mobile-only">🏠</span>Home</a></li>
            <li><a href="produto.jsp"><span class="mobile-only">🛒</span>Shop</a></li>
            <li><a href="catalogo.jsp"><span class="mobile-only">📋</span>Catálogo</a></li>
            <li><a href="sommelia.jsp"><span class="mobile-only">🤖</span>SomelIA</a></li>
            <!-- <li ><a href="index.jsp"><span class="mobile-only">🍷</span>Revista</a></li> -->
            <li><a href="perfil.jsp"><span class="mobile-only">👤</span>Perfil</a></li>
          </ul>
        </nav>
        <!-- <div class="header-icons desktop-only">
          <img
            src="https://placehold.co/20x20"
            alt="Search Icon"
            class="search-icon"
          />
          <img
            src="https://placehold.co/20x20"
            alt="Profile Icon"
            class="profile-icon"
          />
          <img
            src="https://placehold.co/20x20"
            alt="Cart Icon"
            class="cart-icon"
          />
        </div> -->
      </div>
    </header>
