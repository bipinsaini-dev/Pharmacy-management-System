<%
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Welcome | PharmaCare</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
  <style>
    :root { --teal: #0d9488; --dark: #0b3b3a; --mint: #ccfbf1; }
    * { box-sizing: border-box; }
    body { margin: 0; color: #1f2937; font-family: Inter, system-ui, sans-serif; }
    .landing { min-height: 100vh; background: linear-gradient(135deg, #f0fdfa 0%, #fff 48%, #e0f2fe 100%); }
    .landing-nav { padding: 1.25rem 0; }
    .brand { color: var(--dark); font-size: 1.45rem; font-weight: 800; text-decoration: none; }
    .brand i { color: var(--teal); margin-right: .45rem; }
    .hero { min-height: calc(100vh - 86px); display: flex; align-items: center; padding: 3rem 0 5rem; }
    .eyebrow { color: var(--teal); font-size: .8rem; font-weight: 700; letter-spacing: .12em; text-transform: uppercase; }
    h1 { color: var(--dark); font-size: clamp(2.5rem, 6vw, 4.8rem); font-weight: 800; letter-spacing: -.05em; line-height: 1.05; }
    .lead { color: #52636b; font-size: 1.1rem; line-height: 1.75; max-width: 570px; }
    .hero-art { position: relative; display: flex; align-items: center; justify-content: center; min-height: 360px; }
    .circle { width: 300px; height: 300px; border-radius: 50%; background: linear-gradient(145deg, #99f6e4, #bae6fd); box-shadow: 0 25px 60px rgba(13,148,136,.2); display: flex; align-items: center; justify-content: center; }
    .circle i { color: var(--dark); font-size: 8rem; }
    .floating-card { position: absolute; background: #fff; border-radius: 1rem; padding: .85rem 1rem; box-shadow: 0 12px 30px rgba(15,23,42,.12); font-size: .85rem; font-weight: 600; }
    .floating-card i { color: var(--teal); margin-right: .4rem; }
    .card-one { top: 10%; left: 5%; } .card-two { right: 2%; bottom: 12%; }
    .feature { border: 0; border-radius: 1rem; background: rgba(255,255,255,.78); box-shadow: 0 4px 20px rgba(15,23,42,.06); height: 100%; }
    .feature i { color: var(--teal); font-size: 1.45rem; }
    @media (max-width: 767px) { .hero { padding-top: 1rem; } .hero-art { min-height: 290px; margin-top: 1rem; } .circle { width: 230px; height: 230px; } .circle i { font-size: 6rem; } .card-one { left: 0; } }
  </style>
</head>
<body>
  <div class="landing">
    <nav class="landing-nav">
      <div class="container d-flex align-items-center justify-content-between">
        <a class="brand" href="<%= ctx %>/"><i class="bi bi-capsule-pill"></i>PharmaCare</a>
        <div class="d-flex gap-2">
          <a class="btn btn-outline-success" href="<%= ctx %>/login">Login</a>
          <a class="btn btn-success" href="<%= ctx %>/register">Register</a>
        </div>
      </div>
    </nav>

    <main class="hero">
      <div class="container">
        <div class="row align-items-center g-5">
          <div class="col-lg-6">
            <div class="eyebrow mb-3">Simple. Secure. Smarter pharmacy care.</div>
            <h1 class="mb-4">Manage your pharmacy with confidence.</h1>
            <p class="lead mb-4">PharmaCare brings inventory, billing, customers, purchases, and reports together in one easy-to-use workspace.</p>
            <div class="d-flex flex-wrap gap-3">
              <a class="btn btn-success btn-lg px-4" href="<%= ctx %>/login"><i class="bi bi-box-arrow-in-right me-2"></i>Get started</a>
              <a class="btn btn-outline-dark btn-lg px-4" href="<%= ctx %>/register"><i class="bi bi-person-plus me-2"></i>Create account</a>
            </div>
          </div>
          <div class="col-lg-6">
            <div class="hero-art">
              <div class="circle"><i class="bi bi-capsule-pill"></i></div>
              <div class="floating-card card-one"><i class="bi bi-check-circle-fill"></i>Inventory under control</div>
              <div class="floating-card card-two"><i class="bi bi-receipt"></i>Fast, accurate billing</div>
            </div>
          </div>
        </div>
        <div class="row g-3 mt-4">
          <div class="col-md-4"><div class="feature p-3"><i class="bi bi-box-seam"></i><h2 class="h6 mt-2 mb-1">Inventory management</h2><p class="small text-muted mb-0">Track stock, expiry dates, suppliers, and reorder levels.</p></div></div>
          <div class="col-md-4"><div class="feature p-3"><i class="bi bi-receipt-cutoff"></i><h2 class="h6 mt-2 mb-1">Effortless billing</h2><p class="small text-muted mb-0">Create bills quickly with reliable totals and invoices.</p></div></div>
          <div class="col-md-4"><div class="feature p-3"><i class="bi bi-bar-chart-line"></i><h2 class="h6 mt-2 mb-1">Useful reports</h2><p class="small text-muted mb-0">See sales, purchases, and pharmacy performance clearly.</p></div></div>
        </div>
      </div>
    </main>
  </div>
</body>
</html>
