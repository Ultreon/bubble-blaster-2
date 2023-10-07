package com.ultreon.bubbles.platform.desktop.imgui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.ultreon.bubbles.BubbleBlaster;
import com.ultreon.bubbles.render.Color;
import com.ultreon.bubbles.render.Renderer;
import com.ultreon.bubbles.render.gui.GuiComponent;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.glfwInit;

public class ImGuiRenderer {
    private static final ImBoolean SHOW_INFO_WINDOW = new ImBoolean(false);
    private static final ImBoolean SHOW_FPS_GRAPH = new ImBoolean(false);
    private static final ImBoolean SHOW_ENTITY_EDITOR = new ImBoolean(false);
    private static final ImBoolean SHOW_GUI_MODIFIER = new ImBoolean(false);
    public static final ImBoolean DEBUG_GUI_OPEN = new ImBoolean(false);
    public static final ImBoolean SHOW_COLLISIONS_SHAPES = new ImBoolean(false);
    private static final ImBoolean SHOW_DEBUG_UTILS = new ImBoolean(FabricLoader.getInstance().isDevelopmentEnvironment());

    private static final ImGuiRenderer INSTANCE = new ImGuiRenderer();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();

    public static void render(Renderer renderer) {
        if (SHOW_DEBUG_UTILS.get()) {
            INSTANCE.renderImGui(renderer);
        }
    }

    public static void init() {
        // Pre-init ImGui
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        // Initialize ImGui
        ImGui.createContext();
        final var io = ImGui.getIO();
        io.setIniFilename(null);
        io.getFonts().addFontDefault();

        var windowHandle = ((Lwjgl3Graphics) Gdx.graphics).getWindow().getWindowHandle();

        INSTANCE.imGuiGlfw.init(windowHandle, true);
        INSTANCE.imGuiGl3.init("#version 150");

    }

    public static void dispose() {
        INSTANCE.imGuiGl3.dispose();
        INSTANCE.imGuiGlfw.dispose();
        ImGui.destroyContext();
    }

    private void renderImGui(Renderer renderer) {
        // render 3D scene
        this.imGuiGlfw.newFrame();

        ImGui.newFrame();
        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowSize(Gdx.graphics.getWidth(), 18);
        ImGui.setNextWindowCollapsed(true);

        if (ImGui.begin("BB DebugUtils", ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.AlwaysAutoResize |
                ImGuiWindowFlags.NoTitleBar |
                ImGuiWindowFlags.MenuBar |
                ImGuiInputTextFlags.AllowTabInput)) {
            if (ImGui.beginMenuBar()) {
                if (ImGui.beginMenu("View")) {
                    ImGui.menuItem("Show Info Window", null, SHOW_INFO_WINDOW);
                    ImGui.menuItem("Show FPS Graph", null, SHOW_FPS_GRAPH);
                    ImGui.endMenu();
                }
                if (ImGui.beginMenu("Entity")) {
                    ImGui.menuItem("Show Collisions Shapes", null, SHOW_COLLISIONS_SHAPES);
                    ImGui.menuItem("Entity Editor", null, SHOW_ENTITY_EDITOR, BubbleBlaster.getInstance().isInGame());
                    ImGui.endMenu();
                }
                if (ImGui.beginMenu("GUI")) {
                    ImGui.menuItem("Show Debug Overlay", null, DEBUG_GUI_OPEN);
                    ImGui.menuItem("GUI Modifier", null, SHOW_GUI_MODIFIER);
                    ImGui.endMenu();
                }
                ImGui.endMenuBar();
            }
            ImGui.end();
        }
        if (SHOW_INFO_WINDOW.get()) {
            this.showInfoWindow();
        }
        if (SHOW_GUI_MODIFIER.get()) {
            this.showGuiModifier(renderer);
        }
        ImGui.render();
        this.imGuiGl3.renderDrawData(ImGui.getDrawData());
    }

    private void showGuiModifier(Renderer renderer) {
        var screen = BubbleBlaster.getInstance().getCurrentScreen();
        GuiComponent exactWidgetAt = null;
        if (screen != null) exactWidgetAt = screen.getExactWidgetAt(Gdx.input.getX(), Gdx.input.getY());

        if (exactWidgetAt != null) {
            var bounds = exactWidgetAt.getBounds();
            renderer.box(bounds.x, bounds.y, bounds.width, bounds.height, Color.RED);
        }

        ImGui.setNextWindowSize(400, 200, ImGuiCond.Once);
        ImGui.setNextWindowPos(ImGui.getMainViewport().getPosX() + 100, ImGui.getMainViewport().getPosY() + 100, ImGuiCond.Once);
        if (ImGui.begin("Gui Utilities")) {
            ImGui.text("Screen: " + (screen == null ? "null" : screen.getClass().getSimpleName()));
            ImGui.text("Widget: " + (exactWidgetAt == null ? "null" : exactWidgetAt.getClass().getSimpleName()));
        }
        ImGui.end();
    }

    private void showInfoWindow() {
        var screen = BubbleBlaster.getInstance().getCurrentScreen();
        ImGui.setNextWindowSize(400, 200, ImGuiCond.Once);
        ImGui.setNextWindowPos(ImGui.getMainViewport().getPosX() + 100, ImGui.getMainViewport().getPosY() + 100, ImGuiCond.Once);
        if (ImGui.begin("Debug Info")) {
            ImGui.button("I'm a Button!");
            ImGui.text("Screen:" + (screen == null ? "null" : screen.getClass().getName()));
        }
        ImGui.end();
    }
}
