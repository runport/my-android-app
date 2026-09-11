#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import re
from pathlib import Path

G, Y, R, B, RST = "\033[92m", "\033[93m", "\033[91m", "\033[94m", "\033[0m"
log  = lambda m: print(f"{G}✓{RST} {m}")
warn = lambda m: print(f"{Y}⚠{RST} {m}")
err  = lambda m: print(f"{R}✗{RST} {m}")
info = lambda m: print(f"{B}ℹ{RST} {m}")

BASE = Path("app/src/main/java/com/example")
VM = BASE / "viewmodel/ManufacturingViewModel.kt"
MORE = BASE / "ui/screens/MoreHubScreen.kt"

def read(p): return p.read_text(encoding="utf-8") if p.exists() else ""
def write(p, c): p.write_text(c, encoding="utf-8")

# ============================================
# ۱. افزودن READY_GOODS به enum MoreSubSection
# ============================================
info("۱. افزودن READY_GOODS به MoreSubSection")

c = read(VM)
if "READY_GOODS(" in c:
    warn("READY_GOODS از قبل هست")
else:
    # پیدا کردن enum MoreSubSection
    m = re.search(r'enum class MoreSubSection\(val title: String\)\s*\{', c)
    if m:
        # اضافه کردن بعد از CUTTING یا آخر enum
        if '  CUTTING("برش"),' in c:
            c = c.replace(
                '  CUTTING("برش"),',
                '  CUTTING("برش"),\n  READY_GOODS("کارهای آماده"),',
                1
            )
            write(VM, c)
            log("READY_GOODS اضافه شد")
        else:
            err("CUTTING در enum MoreSubSection پیدا نشد")
    else:
        err("enum MoreSubSection پیدا نشد")

# ============================================
# ۲. افزودن import و case به MoreHubScreen
# ============================================
info("۲. افزودن case به MoreHubScreen")

c = read(MORE)

# import
if "import com.example.ui.screens.ReadyGoodsScreen" not in c:
    # اضافه کردن بعد از imports
    idx = c.rfind("\nimport ")
    end_of_line = c.find("\n", idx + 1)
    c = c[:end_of_line] + "\nimport com.example.ui.screens.ReadyGoodsScreen" + c[end_of_line:]
    log("import ReadyGoodsScreen اضافه شد")

# case در when
if "MoreSubSection.READY_GOODS ->" in c:
    warn("case قبلاً اضافه شده")
else:
    # پیدا کردن اولین case در when(selectedSubSection)
    anchor = "      MoreSubSection.CUTTING -> {"
    if anchor in c:
        new_case = """      MoreSubSection.READY_GOODS -> {
        item {
          ReadyGoodsScreen(viewModel = viewModel)
        }
      }

      MoreSubSection.CUTTING -> {"""
        c = c.replace(anchor, new_case, 1)
        log("case READY_GOODS اضافه شد")
    else:
        err("anchor CUTTING در when پیدا نشد")

# ============================================
# ۳. افزودن دکمه رزرو سفارش به بخش سفارشات
# ============================================
info("۳. افزودن دکمه رزرو به بخش سفارشات")

if "openReserveOrderDialog" in c:
    warn("دکمه رزرو از قبل هست")
else:
    # پیدا کردن header سفارشات
    anchor = '''            Text("لیست سفارشات مشتریان و رهگیری", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
            Button(
              onClick = { viewModel.openQuickAction(QuickActionType.SALE) },
              colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text("سفارش جدید", style = MaterialTheme.typography.labelSmall)
            }'''
    
    replacement = '''            Text("لیست سفارشات مشتریان و رهگیری", style = MaterialTheme.typography.titleSmall, color = customColors.textPrimary, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Button(
                onClick = { viewModel.openReserveOrderDialog() },
                colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text("رزرو جدید", style = MaterialTheme.typography.labelSmall)
              }
              Button(
                onClick = { viewModel.openQuickAction(QuickActionType.SALE) },
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text("فروش فوری", style = MaterialTheme.typography.labelSmall)
              }
            }'''
    
    if anchor in c:
        c = c.replace(anchor, replacement, 1)
        log("دکمه رزرو اضافه شد")
    else:
        warn("anchor سفارشات پیدا نشد — بررسی دستی")

write(MORE, c)

# ============================================
# ۴. اتصال ReserveOrderDialog به MainScreen (اگر وجود دارد)
# ============================================
info("۴. بررسی نمایش ReserveOrderDialog")

MAIN_SCREEN = BASE / "ui/screens/MainScreen.kt"
if MAIN_SCREEN.exists():
    mc = read(MAIN_SCREEN)
    if "ReserveOrderDialog" in mc:
        warn("قبلاً اضافه شده")
    else:
        if "QuickActionsModalBottomSheet" in mc:
            # اضافه کردن import و dialog
            if "import com.example.ui.dialogs.ReserveOrderDialog" not in mc:
                idx = mc.rfind("\nimport ")
                end = mc.find("\n", idx + 1)
                mc = mc[:end] + "\nimport com.example.ui.dialogs.ReserveOrderDialog" + mc[end:]
            
            # پیدا کردن جای QuickActionsModalBottomSheet
            anchor_q = "QuickActionsModalBottomSheet("
            if anchor_q in mc:
                # قبل از آن dialog را اضافه کنیم
                show_dialog = '''  // دیالوگ رزرو سفارش
  val showReserveDialog by viewModel.showReserveOrderDialog.collectAsState()
  if (showReserveDialog) {
    ReserveOrderDialog(
      viewModel = viewModel,
      onDismiss = { viewModel.closeReserveOrderDialog() }
    )
  }

  '''
                # جستجوی موقعیت
                idx_anchor = mc.find(anchor_q)
                if idx_anchor > 0:
                    # پیدا کردن ابتدای خط
                    line_start = mc.rfind("\n", 0, idx_anchor) + 1
                    mc = mc[:line_start] + show_dialog + mc[line_start:]
                    write(MAIN_SCREEN, mc)
                    log("ReserveOrderDialog به MainScreen اضافه شد")
        else:
            warn("MainScreen پیدا شد ولی QuickActionsModalBottomSheet ندارد")
else:
    warn("MainScreen پیدا نشد — بررسی ساختار پروژه لازم")

# ============================================
# ۵. اعتبارسنجی
# ============================================
print()
info("۵. بررسی آکولاد:")
for path, label in [(VM, "ViewModel"), (MORE, "MoreHubScreen")]:
    cc = read(path)
    ob, cb = cc.count("{"), cc.count("}")
    if ob == cb:
        log(f"{label}: {ob} متوازن")
    else:
        err(f"{label}: {ob} vs {cb} نامتوازن!")

if MAIN_SCREEN.exists():
    mc = read(MAIN_SCREEN)
    ob, cb = mc.count("{"), mc.count("}")
    if ob == cb:
        log(f"MainScreen: {ob} متوازن")
    else:
        err(f"MainScreen: {ob} vs {cb} نامتوازن!")

print()
log("فاز ۴.۳ اعمال شد")
print()
info("مرحله بعد:")
print(f"  {B}git add . && git commit -m 'feat(phase4.3): navigation + reserve order integration' && git push origin feature/cutting-parts-workflow{RST}")
