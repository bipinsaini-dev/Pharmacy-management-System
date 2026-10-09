<c:set var="pageTitle" value="New Bill" />
<c:set var="activeNav" value="billing" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="row g-3">
  <%-- left: medicine search --%>
  <div class="col-lg-6">
    <div class="card h-100">
      <div class="card-header">Find medicine</div>
      <div class="card-body">
        <form method="get" action="${ctx}/${base}/billing" class="input-group mb-3">
          <input type="search" class="form-control" name="q" value="<c:out value='${q}'/>" placeholder="Search by name, generic name or batch" autofocus>
          <button class="btn btn-primary" type="submit"><i class="bi bi-search"></i></button>
        </form>
        <div class="table-responsive">
          <table class="table table-sm align-middle">
            <thead><tr><th>Medicine</th><th>Batch</th><th class="text-end">Price</th><th class="text-end">Stock</th><th style="width:9.5rem">Qty</th></tr></thead>
            <tbody>
              <c:forEach var="m" items="${results}">
                <tr>
                  <td><strong><c:out value="${m.medicineName}" /></strong><br><small class="text-muted"><c:out value="${m.genericName}" /></small></td>
                  <td><c:out value="${m.batchNumber}" /><br><small class="text-muted">Exp <fmt:formatDate value="${m.expiryDate}" pattern="MMM yyyy" /></small></td>
                  <td class="text-end">&#8377;<fmt:formatNumber value="${m.sellingPrice}" minFractionDigits="2" maxFractionDigits="2" /></td>
                  <td class="text-end"><span class="badge ${m.badgeClass}">${m.stockQuantity}</span></td>
                  <td>
                    <form method="post" action="${ctx}/${base}/billing" class="input-group input-group-sm">
                      <input type="hidden" name="action" value="add">
                      <input type="hidden" name="medicineId" value="${m.id}">
                      <input type="hidden" name="q" value="<c:out value='${q}'/>">
                      <input type="number" class="form-control" name="quantity" value="1" min="1" max="${m.stockQuantity}" required>
                      <button class="btn btn-success" type="submit" title="Add to cart"><i class="bi bi-cart-plus"></i></button>
                    </form>
                  </td>
                </tr>
              </c:forEach>
              <c:if test="${empty results}"><tr><td colspan="5" class="text-center text-muted py-3">No sellable medicines found (in stock and not expired).</td></tr></c:if>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>

  <%-- right: cart --%>
  <div class="col-lg-6">
    <div class="card h-100">
      <div class="card-header d-flex justify-content-between align-items-center">
        <span><i class="bi bi-cart3 me-1"></i>Cart (${fn:length(cartLines)} item(s))</span>
        <button class="btn btn-sm btn-outline-secondary" type="button" data-bs-toggle="modal" data-bs-target="#newCustomerModal"><i class="bi bi-person-plus me-1"></i>New customer</button>
      </div>
      <div class="card-body">
        <form method="post" action="${ctx}/${base}/billing" id="cartForm" data-gst-rate="${gstRate}">
          <div class="mb-3">
            <label class="form-label small mb-1">Customer</label>
            <select class="form-select" name="customerId">
              <c:forEach var="c" items="${customers}">
                <option value="${c.id}" ${c.id == selectedCustomerId ? 'selected' : ''}><c:out value="${c.customerName}" /><c:if test="${not empty c.phone}"> (<c:out value="${c.phone}" />)</c:if></option>
              </c:forEach>
            </select>
          </div>
          <div class="table-responsive">
            <table class="table table-sm align-middle">
              <thead><tr><th>Medicine</th><th class="text-end">Price</th><th style="width:6rem">Qty</th><th class="text-end">Total</th><th></th></tr></thead>
              <tbody>
                <c:forEach var="l" items="${cartLines}">
                  <tr class="cart-row" data-price="${l.price}">
                    <td><c:out value="${l.medicineName}" /><br><small class="text-muted">Batch <c:out value="${l.batchNumber}" /></small></td>
                    <td class="text-end">&#8377;<fmt:formatNumber value="${l.price}" minFractionDigits="2" maxFractionDigits="2" /></td>
                    <td>
                      <input type="hidden" name="medicineId" value="${l.medicineId}">
                      <input type="number" class="form-control form-control-sm cart-qty" name="quantity" value="${l.quantity}" min="0" max="${l.availableStock}">
                    </td>
                    <td class="text-end line-total">&#8377;<fmt:formatNumber value="${l.total}" minFractionDigits="2" maxFractionDigits="2" /></td>
                    <td class="text-end"><button class="btn btn-sm btn-outline-danger" type="submit" name="action" value="remove-${l.medicineId}" formnovalidate title="Remove"><i class="bi bi-x-lg"></i></button></td>
                  </tr>
                </c:forEach>
                <c:if test="${empty cartLines}"><tr><td colspan="5" class="text-center text-muted py-3">The cart is empty. Add medicines from the left.</td></tr></c:if>
              </tbody>
            </table>
          </div>

          <div class="row g-2 mb-3">
            <div class="col-6">
              <label class="form-label small mb-1">Discount (%)</label>
              <input type="number" class="form-control" name="discountPercent" id="discountPercent" min="0" max="100" step="0.01" value="${discountPct}">
            </div>
            <div class="col-6">
              <label class="form-label small mb-1">Payment method</label>
              <select class="form-select" name="paymentMethod">
                <option ${payment eq 'Cash' ? 'selected' : ''}>Cash</option>
                <option ${payment eq 'Card' ? 'selected' : ''}>Card</option>
                <option ${payment eq 'UPI' ? 'selected' : ''}>UPI</option>
              </select>
            </div>
          </div>

          <ul class="list-group mb-3 totals">
            <li class="list-group-item d-flex justify-content-between"><span>Subtotal</span><span>&#8377;<span id="tSubtotal"><fmt:formatNumber value="${totals.subtotal}" minFractionDigits="2" maxFractionDigits="2" /></span></span></li>
            <li class="list-group-item d-flex justify-content-between"><span>Discount</span><span>- &#8377;<span id="tDiscount"><fmt:formatNumber value="${totals.discount}" minFractionDigits="2" maxFractionDigits="2" /></span></span></li>
            <li class="list-group-item d-flex justify-content-between"><span>GST (${gstRate}%)</span><span>&#8377;<span id="tGst"><fmt:formatNumber value="${totals.gst}" minFractionDigits="2" maxFractionDigits="2" /></span></span></li>
            <li class="list-group-item d-flex justify-content-between fw-bold fs-5"><span>Grand total</span><span>&#8377;<span id="tGrand"><fmt:formatNumber value="${totals.grandTotal}" minFractionDigits="2" maxFractionDigits="2" /></span></span></li>
          </ul>

          <div class="d-flex flex-wrap gap-2">
            <button class="btn btn-outline-primary" type="submit" name="action" value="update"><i class="bi bi-arrow-repeat me-1"></i>Update cart</button>
            <button class="btn btn-outline-secondary" type="submit" name="action" value="clear" formnovalidate>Clear</button>
            <button class="btn btn-primary ms-auto" type="submit" name="action" value="checkout" ${empty cartLines ? 'disabled' : ''}><i class="bi bi-check2-circle me-1"></i>Complete sale</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</div>

<div class="modal fade crud-modal" id="newCustomerModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <form class="modal-content needs-validation" method="post" action="${ctx}/${base}/customers" novalidate>
      <input type="hidden" name="action" value="save">
      <input type="hidden" name="id" value="0">
      <input type="hidden" name="returnTo" value="billing">
      <div class="modal-header"><h5 class="modal-title">New customer</h5><button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button></div>
      <div class="modal-body">
        <div class="mb-3"><label class="form-label">Name *</label><input class="form-control" name="customerName" required maxlength="100"><div class="invalid-feedback">Name is required.</div></div>
        <div class="mb-3"><label class="form-label">Phone</label><input class="form-control" name="phone" pattern="\+?[0-9]{10,13}"><div class="invalid-feedback">Enter 10 to 13 digits.</div></div>
        <div class="mb-3"><label class="form-label">Email</label><input type="email" class="form-control" name="email"></div>
        <div class="mb-0"><label class="form-label">Address</label><textarea class="form-control" name="address" rows="2"></textarea></div>
      </div>
      <div class="modal-footer"><button type="button" class="btn btn-light" data-bs-dismiss="modal">Cancel</button><button type="submit" class="btn btn-primary">Save &amp; select</button></div>
    </form>
  </div>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
